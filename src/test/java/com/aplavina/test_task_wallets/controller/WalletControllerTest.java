package com.aplavina.test_task_wallets.controller;

import com.aplavina.test_task_wallets.dto.ChangeWalletBalanceDto;
import com.aplavina.test_task_wallets.model.wallet.OperationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.aplavina.test_task_wallets.model.wallet.OperationType.DEPOSIT;
import static com.aplavina.test_task_wallets.model.wallet.OperationType.WITHDRAW;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.3");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) throws InterruptedException {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        Thread.sleep(1000);
    }

    @ParameterizedTest
    @MethodSource("correctBalanceUpdateData")
    void testBalanceUpdate(long initialBalance, long amount, long expectedBalance, OperationType operationType) throws Exception {
        UUID id = createWalletWithBalanceInDb(initialBalance);
        ChangeWalletBalanceDto changeWalletBalanceDto = new ChangeWalletBalanceDto(
                id, operationType, amount);

        MvcResult result = mockMvc.perform(
                post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeWalletBalanceDto))
        ).andExpect(status().isOk()).andReturn();

        long newBalance = Long.parseLong(result.getResponse().getContentAsString());
        assertEquals(expectedBalance, newBalance);
        long balanceInDb = getBalanceFromDb(id);
        assertEquals(expectedBalance, balanceInDb);
    }

    @Test
    void testWithdrawWithInsufficientBalance() throws Exception {
        long initialBalance = 1000L;
        UUID id = createWalletWithBalanceInDb(initialBalance);
        ChangeWalletBalanceDto changeWalletBalanceDto
                = new ChangeWalletBalanceDto(id, OperationType.WITHDRAW, 10000L);
        mockMvc.perform(
                post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeWalletBalanceDto))
        ).andExpect(status().isBadRequest());
        long balanceInDb = getBalanceFromDb(id);
        assertEquals(initialBalance, balanceInDb);
    }

    @RepeatedTest(10)
    void testConcurrentBalanceUpdate() {
        ExecutorService pool = Executors.newFixedThreadPool(16);
        long initialBalance = 2000L;
        UUID walletId = createWalletWithBalanceInDb(initialBalance);
        CompletableFuture<Void>[] deposits = new CompletableFuture[500];
        CompletableFuture<Void>[] withdraws = new CompletableFuture[500];
        for (int i = 0; i < 500; i++) {
            deposits[i] = performBalanceUpdate(walletId, 2L, OperationType.DEPOSIT, pool);
            withdraws[i] = performBalanceUpdate(walletId, 2L, OperationType.WITHDRAW, pool);
        }
        CompletableFuture<Void> allDeposits = CompletableFuture.allOf(deposits);
        CompletableFuture<Void> allWithdraws = CompletableFuture.allOf(withdraws);
        CompletableFuture.allOf(allDeposits, allWithdraws).join();
        long finalBalance = getBalanceFromDb(walletId);
        assertEquals(initialBalance, finalBalance);
    }

    private CompletableFuture<Void> performBalanceUpdate(
            UUID walletId,
            long amount,
            OperationType operationType,
            ExecutorService pool) {
        ChangeWalletBalanceDto changeWalletBalanceDto = new ChangeWalletBalanceDto(walletId, operationType, amount);

        return CompletableFuture.runAsync(() -> {
            try {
                mockMvc.perform(
                        post("/api/v1/wallets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeWalletBalanceDto))
                ).andExpect(status().isOk()).andReturn();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Error performing balance update: " + e.getMessage());
            }
        }, pool);
    }

    private long getBalanceFromDb(UUID walletId) {
        return jdbcTemplate.queryForObject(
                "SELECT balance FROM wallet WHERE id = ?",
                Long.class,
                walletId
        );
    }

    private UUID createWalletWithBalanceInDb(long initialBalance) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO wallet (balance) VALUES (?) RETURNING id",
                new Object[]{initialBalance},
                (rs, rowNum) -> UUID.fromString(rs.getString("id"))
        );
    }

    private static Stream<Arguments> correctBalanceUpdateData() {
        return Stream.of(
                Arguments.of(1000L, 100L, 1100L, DEPOSIT),
                Arguments.of(1000L, 200L, 800L, WITHDRAW),
                Arguments.of(1000L, 500L, 500L, WITHDRAW)
        );
    }
}