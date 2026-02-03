package com.cadt.sortoutjobbackend.job.repository;

import com.cadt.sortoutjobbackend.job.entity.Job;
import com.cadt.sortoutjobbackend.job.entity.LocationType;
import com.cadt.sortoutjobbackend.profile.entity.EmploymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Find all active jobs ordered by posted date (newest first)
    Page<Job> findByIsActiveTrueOrderByPostedAtDesc(Pageable pageable);

    // Find active jobs by role
    Page<Job> findByIsActiveTrueAndRoleIdOrderByPostedAtDesc(Long roleId, Pageable pageable);

    // Find active jobs by city
    Page<Job> findByIsActiveTrueAndCityIdOrderByPostedAtDesc(Long cityId, Pageable pageable);

    // Find active jobs by location type (REMOTE, ONSITE, HYBRID)
    Page<Job> findByIsActiveTrueAndLocationTypeOrderByPostedAtDesc(LocationType locationType, Pageable pageable);

    // Find active jobs by employment type
    Page<Job> findByIsActiveTrueAndEmploymentTypeOrderByPostedAtDesc(EmploymentType employmentType, Pageable pageable);

    // Search jobs by keyword (title, company, description)
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY j.postedAt DESC")
    Page<Job> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Advanced search with multiple filters
    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN j.requiredSkills s WHERE j.isActive = true " +
           "AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "    OR LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:cityId IS NULL OR j.city.id = :cityId) " +
           "AND (:roleId IS NULL OR j.role.id = :roleId) " +
           "AND (:locationType IS NULL OR j.locationType = :locationType) " +
           "AND (:employmentType IS NULL OR j.employmentType = :employmentType) " +
           "AND (:minExperience IS NULL OR j.experienceMinYears <= :minExperience) " +
           "AND (:maxExperience IS NULL OR j.experienceMaxYears IS NULL OR j.experienceMaxYears >= :maxExperience) " +
           "ORDER BY j.postedAt DESC")
    Page<Job> searchJobs(
        @Param("keyword") String keyword,
        @Param("cityId") Long cityId,
        @Param("roleId") Long roleId,
        @Param("locationType") LocationType locationType,
        @Param("employmentType") EmploymentType employmentType,
        @Param("minExperience") Integer minExperience,
        @Param("maxExperience") Integer maxExperience,
        Pageable pageable
    );

    // Find jobs matching user skills (for recommendations)
    @Query("SELECT DISTINCT j FROM Job j JOIN j.requiredSkills s WHERE j.isActive = true " +
           "AND s.id IN :skillIds " +
           "ORDER BY j.postedAt DESC")
    Page<Job> findBySkillIds(@Param("skillIds") Set<Long> skillIds, Pageable pageable);

    // Find jobs matching user's preferred role
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND j.role.id = :roleId " +
           "ORDER BY j.isFeatured DESC, j.postedAt DESC")
    Page<Job> findRecommendedByRole(@Param("roleId") Long roleId, Pageable pageable);

    // Count active jobs
    long countByIsActiveTrue();

    // Count jobs posted today
    @Query("SELECT COUNT(j) FROM Job j WHERE j.isActive = true AND j.postedAt >= :since")
    long countJobsPostedSince(@Param("since") Instant since);

    // Find featured jobs
    Page<Job> findByIsActiveTrueAndIsFeaturedTrueOrderByPostedAtDesc(Pageable pageable);

    // Find jobs posted by a specific user (admin/employer)
    Page<Job> findByPostedByIdOrderByPostedAtDesc(Long userId, Pageable pageable);
}
