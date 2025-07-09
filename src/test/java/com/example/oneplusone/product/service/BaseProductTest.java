package com.example.oneplusone.product.service;

import com.example.oneplusone.domain.auth.entity.User;
import com.example.oneplusone.domain.auth.enums.UserRole;
import com.example.oneplusone.domain.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BaseProductTest {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserRepository userRepository;

    private static final int COUNT = 50000;
    private static final int BATCH_SIZE = 2000;
    private static final String INSERT_SQL = "INSERT INTO products (seller_id, name, type, price, quantity) values (?, ?, ?, ?, ?)";

    @DisplayName("상품 대용령 대이터 생성")
    @Test
    protected void initProduct() throws InterruptedException {
        Long userId = createUser();

        int size = COUNT / BATCH_SIZE;
        int threadPoolSize = Runtime.getRuntime().availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(size);

        for (int i = 0; i < size; i++) {
            final int batch = i;
            executor.submit(() -> {
                try {
                    batchInsert(batch, userId);
                } finally {
                    System.out.println("Batch " + batch + ": end");
                    latch.countDown();
                }
            });
        }

        // 모든 작업이 완료될 때까지 대기
        latch.await();
        executor.shutdown();

        // 생성된 갯수 확인
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Long.class);
        System.out.println("Create Products : " + count);
    }

    private Long createUser() {
        User user = new User("nickname", "login", "password", UserRole.SELLER);
        userRepository.save(user);
        return user.getId();
    }

    private void batchInsert(int batch, Long userId) {
        jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int num = batch * BATCH_SIZE + i;

                ps.setLong(1, userId); //seller_id
                ps.setString(2, "productName" + num); //name
                ps.setString(3, "상품종류"); //type
                ps.setLong(4, 1000); //price
                ps.setLong(5, 10); //quantity
            }

            @Override
            public int getBatchSize() {
                return BATCH_SIZE;
            }
        });
    }
}