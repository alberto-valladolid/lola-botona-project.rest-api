package com.lolabotona.restapi.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lolabotona.restapi.model.User;
import com.lolabotona.restapi.model.UserTeacher;

public interface UserTeacherRepository extends JpaRepository<UserTeacher, Long> {	

	List<UserTeacher> findByUser(User user);	

}