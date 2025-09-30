package org.ever.sd.controller;

import org.ever.sd.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@Import(OrderControllerTest.TestConfig.class)
class OrderControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        OrderService orderService() {
            return Mockito.mock(OrderService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("given valid body when post order then return 201")
    void givenValidBody_whenPostOrder_thenReturn201() throws Exception {
        // given
        doNothing().when(orderService).registerOrder(1L, "상품A", 3);
        String json = "{\"id\":1,\"name\":\"상품A\",\"count\":3}";

        // when
        // then
        mockMvc.perform(post("/api/sd/sales-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }
}


