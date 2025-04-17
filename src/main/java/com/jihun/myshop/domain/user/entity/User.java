package com.jihun.myshop.domain.user.entity;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.product.entity.Review;
import com.jihun.myshop.domain.wishlist.entity.WishlistItem;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @ManyToMany(fetch = FetchType.LAZY, cascade={CascadeType.MERGE})
    @JoinTable(name = "user_roles",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private Set<Role> userRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WishlistItem> wishlistItems = new ArrayList<>();


    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void addRole(Role role) {
        if (this.userRoles == null) {
            this.userRoles = new HashSet<>();
        }
        this.userRoles.add(role);
    }

    public void clearRoles() {
        if (this.userRoles != null) {
            this.userRoles.clear();
        } else {
            this.userRoles = new HashSet<>();
        }
    }
}
