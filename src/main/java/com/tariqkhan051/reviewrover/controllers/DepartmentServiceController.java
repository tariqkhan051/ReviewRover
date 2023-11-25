package com.tariqkhan051.reviewrover.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tariqkhan051.reviewrover.helpers.Utils;
import com.tariqkhan051.reviewrover.helpers.Messages.ResponseMessages;
import com.tariqkhan051.reviewrover.models.Department;
import com.tariqkhan051.reviewrover.payload.request.CreateDeparmentRequest;
import com.tariqkhan051.reviewrover.repository.DepartmentRepository;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/test")
public class DepartmentServiceController {

    @Autowired
    DepartmentRepository departmentRepository;

    @CrossOrigin
    @RequestMapping(value = "/departments", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> getDepartments() {
        try {
            var departments = departmentRepository.findAll();

            if (departments.size() > 0) {
                return ResponseMessages.SuccessResponseData(departments, "Departments retrieved successfully.");
            }

            return ResponseMessages.ErrorResponse("No departments found.");

        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to retrieve departments.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/departments", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> addDepartment(@Valid @RequestBody CreateDeparmentRequest createDepartmentRequest) {
        try {
            if (departmentRepository.existsByName(createDepartmentRequest.getName())) {
                return ResponseMessages.ErrorResponse("Department name already exists.");
            }

            if (Utils.IsNullOrEmpty(createDepartmentRequest.getName())) {
                return ResponseMessages.ErrorResponse("Department name is required.");
            }

            Department department = new Department(createDepartmentRequest.getName());
            department.setCreatedOn(Utils.GetCurrentTimeStamp());
            departmentRepository.save(department);

            return ResponseMessages.SuccessResponse("Department added successfully.");

        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to create department.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/departments/{department}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateDepartment(@PathVariable("department") Long id,
            @RequestBody Department updateDepartmentRequest) {

        if (id == null || updateDepartmentRequest.getName().isEmpty()) {
            return ResponseMessages.MissingFieldsResponse();
        }

        try {
            if (departmentRepository.existsByName(updateDepartmentRequest.getName())) {
                return ResponseMessages.SuccessResponse("Department name already exists.");
            }

            var department = departmentRepository.findById(id);

            if (department.isPresent()) {
                Department departmentToUpdate = department.get();
                departmentToUpdate.setName(updateDepartmentRequest.getName());
                departmentRepository.save(departmentToUpdate);

                return ResponseMessages.SuccessResponse("Department updated successfully.");
            }

            return ResponseMessages.ErrorResponse("Department not found.");
        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to update department.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/departments/{department}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteDepartment(@PathVariable("department") Long id) {

        if (id == null) {
            return ResponseMessages.MissingFieldsResponse();
        }

        try {
            var department = departmentRepository.findById(id);

            if (!department.isPresent()) {
                return ResponseMessages.ErrorResponse("Department not found.");
            }

            departmentRepository.delete(department.get());
            return ResponseMessages.SuccessResponse("Department deleted successfully!");
        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to delete department.");
        }
    }
}
