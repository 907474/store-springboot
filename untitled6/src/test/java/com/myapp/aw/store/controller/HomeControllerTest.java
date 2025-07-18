package com.myapp.aw.store.controller;

import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser
    public void whenGetHomepage_thenReturns200AndIndexView() throws Exception {
        Page<com.myapp.aw.store.model.Product> emptyPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.findByStatus(any(), any())).thenReturn(emptyPage);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk()) // Check for HTTP 200 OK status
                .andExpect(view().name("index")) // Check that the view returned is "index"
                .andExpect(model().attributeExists("productPage")); // Check that the model has the "productPage" attribute
    }
}