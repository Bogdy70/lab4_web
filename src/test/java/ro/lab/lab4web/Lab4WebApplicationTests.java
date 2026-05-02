package ro.lab.lab4web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class Lab4WebApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void countProductsReturnsTotalNumberOfProducts() throws Exception {
        mockMvc.perform(get("/api/products/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void searchProductsReturnsProductsFilteredByName() throws Exception {
        mockMvc.perform(get("/api/products/search")
                        .param("name", "top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void findProductsWithPriceLessThanReturnsProductsFilteredByPrice() throws Exception {
        mockMvc.perform(get("/api/products/price-less-than")
                        .param("price", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mouse"))
                .andExpect(jsonPath("$[1].name").value("Caiet"));
    }

    @Test
    void createProductReturnsBadRequestForInvalidData() throws Exception {
        String invalidProduct = """
                {
                    "name": "Invalid",
                    "price": 0,
                    "stock": -1,
                    "category": "Test"
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidProduct))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("price must be strictly positive")))
                .andExpect(jsonPath("$.message", containsString("stock cannot be negative")));
    }

    @Test
    void createProductReturnsProductResponse() throws Exception {
        String validProduct = """
                {
                    "name": "Tastatura",
                    "price": 150,
                    "stock": 10,
                    "category": "Electronice"
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProduct))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Tastatura"))
                .andExpect(jsonPath("$.category").value("Electronice"));
    }
}
