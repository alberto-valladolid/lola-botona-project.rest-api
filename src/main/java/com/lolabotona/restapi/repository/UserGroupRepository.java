package com.lolabotona.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lolabotona.restapi.model.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

}