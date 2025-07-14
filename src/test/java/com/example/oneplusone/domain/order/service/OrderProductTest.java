package com.example.oneplusone.domain.order.service;

import com.example.oneplusone.domain.common.exception.BaseException;
import com.example.oneplusone.domain.common.exception.ErrorCode;
import com.example.oneplusone.domain.orders.dto.request.OrderRequest;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderProductTest extends BaseOrderTest {

    @Autowired
    private ProductRepository productRepository;

    private final AtomicInteger successCount = new AtomicInteger();
    private final AtomicInteger failCount = new AtomicInteger();

    @Test
    protected void 락이_없는_테스트_orderProduct() throws Exception {
        String url = "/orders/products/{productId}";

        Product findProduct = orderProductTest(url);

        assertThat(successCount.get()).isLessThan(100);
        assertThat(failCount.get()).isGreaterThan(0);
        assertThat(findProduct.getQuantity()).isGreaterThan(0);
    }

    @Test
    protected void 배타락이_적용된_테스트_orderProduct() throws Exception {
        String url = "/orders/lock/products/{productId}";

        Product findProduct = orderProductTest(url);

        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(0);
        assertThat(findProduct.getQuantity()).isEqualTo(0);
    }

    @Test
    protected void Lettuce가_적용된_테스트_orderProduct() throws Exception {
        String url = "/orders/lettuce/lock/products/{productId}";

        Product findProduct = orderProductTest(url);

        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(0);
        assertThat(findProduct.getQuantity()).isEqualTo(0);
    }

    @Test
    protected void redisson이_적용된_테스트_orderProduct() throws Exception {
        String url = "/orders/redisson/lock/products/{productId}";

        Product findProduct = orderProductTest(url);

        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(0);
        assertThat(findProduct.getQuantity()).isEqualTo(0);
    }

    protected Product orderProductTest(String url) throws Exception {
        // given
        // 필요 데이터 생성
        OrderRequest orderRequest = new OrderRequest(1L);
        String uniqueLoginId = "loginId_" + System.currentTimeMillis();
        Long productId = getProduct(uniqueLoginId).getId();
        String token = getTokenByLogin(uniqueLoginId);

        // 멀티 쓰레드 설정
        int size = 100;
        int threadPoolSize = Runtime.getRuntime().availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(size);

        // when
        for (int i = 0; i < size; i++) {
            executor.submit(() -> {
                try {
                    int status = getStatusOrderProduct(productId, token, orderRequest, url);
                    if (status == 201) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        // 모든 작업이 완료될 때까지 대기
        latch.await();
        executor.shutdown();

        // then
        return productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}