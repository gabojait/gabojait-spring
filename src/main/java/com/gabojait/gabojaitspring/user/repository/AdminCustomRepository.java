package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCustomRepository {

    Page<Admin> searchUnapprovedOrderByCreatedAt(long id, Pageable pageable);

}
