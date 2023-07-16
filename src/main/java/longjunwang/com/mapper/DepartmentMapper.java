package longjunwang.com.mapper;

import longjunwang.com.entity.Department;

import java.util.List;

/**
 * desc: DepartmentMapper
 *
 * @author ink
 * date:2023-07-08 16:17
 */
public interface DepartmentMapper {

    List<Department> findAll();

    int insert(Department department);

    int update(Department department);

    int deleteById(String id);

    Department findById(String id);
}
