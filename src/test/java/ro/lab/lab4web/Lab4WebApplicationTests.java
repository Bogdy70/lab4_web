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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].category").value("Electronice"))
                .andExpect(jsonPath("$[0].createdAt").exists());
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
                .andExpect(jsonPath("$.category").value("Electronice"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createProductReturnsBadRequestForMissingCategory() throws Exception {
        String invalidProduct = """
                {
                    "name": "Agenda",
                    "price": 20,
                    "stock": 5,
                    "category": ""
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidProduct))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("category is required")));
    }

    @Test
    void updateStockReturnsProductWithUpdatedStock() throws Exception {
        String stockUpdate = """
                {
                    "stock": 15
                }
                """;

        mockMvc.perform(patch("/api/products/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stockUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.stock").value(15))
                .andExpect(jsonPath("$.category").value("Electronice"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void updateStockReturnsBadRequestForInvalidStock() throws Exception {
        String stockUpdate = """
                {
                    "stock": -2
                }
                """;

        mockMvc.perform(patch("/api/products/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stockUpdate))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("stock cannot be negative")));
    }

    @Test
    void invalidJsonReturnsBadRequest() throws Exception {
        String invalidJson = """
                {
                    "name": "Invalid",
                    "price": 10,
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid JSON request body"));
    }
}
