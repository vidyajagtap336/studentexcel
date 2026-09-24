package studentexcel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import studentexcel.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);
}