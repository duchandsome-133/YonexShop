package com.yonex.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId")
    private Long roleId;

    @Column(name = "RoleName", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(name = "Description", length = 255)
    private String description;
}
