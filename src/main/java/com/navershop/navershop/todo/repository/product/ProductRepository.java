package com.navershop.navershop.todo.repository.product;

import com.navershop.navershop.todo.repository.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySellerAndName(User seller, String name);
}
