package com.lolabotona.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lolabotona.restapi.model.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

}
