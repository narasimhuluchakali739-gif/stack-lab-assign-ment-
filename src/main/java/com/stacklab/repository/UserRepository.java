package com.stacklab.repository;
import com.stacklab.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.JpaSpecificationExecutor; import java.util.*;
public interface UserRepository extends JpaRepository<AppUser,Long>,JpaSpecificationExecutor<AppUser>{Optional<AppUser> findByEmailIgnoreCase(String email); boolean existsByEmailIgnoreCase(String email); List<AppUser> findByActiveTrue();}
