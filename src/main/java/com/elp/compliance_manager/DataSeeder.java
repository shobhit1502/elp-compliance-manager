package com.elp.compliance_manager;

import com.elp.compliance_manager.product.Product;
import com.elp.compliance_manager.product.ProductCategory;
import com.elp.compliance_manager.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            log.info("Seeding product catalog...");
            productRepository.saveAll(List.of(
                    Product.builder().name("Windows").version("10").edition("Pro").category(ProductCategory.DESKTOP_OS).description("Windows 10 Professional").build(),
                    Product.builder().name("Windows").version("10").edition("Enterprise").category(ProductCategory.DESKTOP_OS).description("Windows 10 Enterprise").build(),
                    Product.builder().name("Windows").version("11").edition("Pro").category(ProductCategory.DESKTOP_OS).description("Windows 11 Professional").build(),
                    Product.builder().name("Windows").version("11").edition("Enterprise").category(ProductCategory.DESKTOP_OS).description("Windows 11 Enterprise").build(),
                    Product.builder().name("Windows Server").version("2016").edition("Standard").category(ProductCategory.SERVER_OS).description("Windows Server 2016 Standard").build(),
                    Product.builder().name("Windows Server").version("2016").edition("Datacenter").category(ProductCategory.SERVER_OS).description("Windows Server 2016 Datacenter").build(),
                    Product.builder().name("Windows Server").version("2019").edition("Standard").category(ProductCategory.SERVER_OS).description("Windows Server 2019 Standard").build(),
                    Product.builder().name("Windows Server").version("2019").edition("Datacenter").category(ProductCategory.SERVER_OS).description("Windows Server 2019 Datacenter").build(),
                    Product.builder().name("Windows Server").version("2022").edition("Standard").category(ProductCategory.SERVER_OS).description("Windows Server 2022 Standard").build(),
                    Product.builder().name("Windows Server").version("2022").edition("Datacenter").category(ProductCategory.SERVER_OS).description("Windows Server 2022 Datacenter").build(),
                    Product.builder().name("SQL Server").version("2016").edition("Standard").category(ProductCategory.DATABASE).description("SQL Server 2016 Standard").build(),
                    Product.builder().name("SQL Server").version("2016").edition("Enterprise").category(ProductCategory.DATABASE).description("SQL Server 2016 Enterprise").build(),
                    Product.builder().name("SQL Server").version("2019").edition("Standard").category(ProductCategory.DATABASE).description("SQL Server 2019 Standard").build(),
                    Product.builder().name("SQL Server").version("2019").edition("Enterprise").category(ProductCategory.DATABASE).description("SQL Server 2019 Enterprise").build(),
                    Product.builder().name("SQL Server").version("2022").edition("Standard").category(ProductCategory.DATABASE).description("SQL Server 2022 Standard").build(),
                    Product.builder().name("Exchange Server").version("2016").edition("Standard").category(ProductCategory.MESSAGING).description("Exchange Server 2016 Standard").build(),
                    Product.builder().name("Exchange Server").version("2019").edition("Standard").category(ProductCategory.MESSAGING).description("Exchange Server 2019 Standard").build(),
                    Product.builder().name("Visio").version("2019").edition("Standard").category(ProductCategory.PRODUCTIVITY).description("Visio 2019 Standard").build(),
                    Product.builder().name("Visio").version("2019").edition("Professional").category(ProductCategory.PRODUCTIVITY).description("Visio 2019 Professional").build(),
                    Product.builder().name("Project").version("2019").edition("Standard").category(ProductCategory.PRODUCTIVITY).description("Project 2019 Standard").build(),
                    Product.builder().name("Project").version("2019").edition("Professional").category(ProductCategory.PRODUCTIVITY).description("Project 2019 Professional").build(),
                    Product.builder().name("Visual Studio").version("2019").edition("Professional").category(ProductCategory.DEVELOPER_TOOLS).description("Visual Studio 2019 Professional").build(),
                    Product.builder().name("Visual Studio").version("2019").edition("Enterprise").category(ProductCategory.DEVELOPER_TOOLS).description("Visual Studio 2019 Enterprise").build()
            ));
            log.info("Product catalog seeded with {} products", productRepository.count());
        } else {
            log.info("Product catalog already seeded. Skipping.");
        }
    }
}