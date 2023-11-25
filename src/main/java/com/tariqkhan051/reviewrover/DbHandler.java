package com.tariqkhan051.reviewrover;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tariqkhan051.reviewrover.converters.Convert;
import com.tariqkhan051.reviewrover.helpers.Utils;
import com.tariqkhan051.reviewrover.models.DepartmentScore;
import com.tariqkhan051.reviewrover.models.EReviewStatus;
import com.tariqkhan051.reviewrover.models.Job;
import com.tariqkhan051.reviewrover.models.MonthlyReview;
import com.tariqkhan051.reviewrover.models.PendingReview;
import com.tariqkhan051.reviewrover.models.Review;
import com.tariqkhan051.reviewrover.models.Score;
import com.tariqkhan051.reviewrover.models.Scores;
import com.tariqkhan051.reviewrover.models.Team;
import com.tariqkhan051.reviewrover.models.TeamScore;
import com.tariqkhan051.reviewrover.models.User;
import com.tariqkhan051.reviewrover.models.UserScore;
import com.tariqkhan051.reviewrover.models.external.GetJobsResponse;
import com.tariqkhan051.reviewrover.models.external.GetReviewsRequest;
import com.tariqkhan051.reviewrover.models.external.GetReviewsResponse;
import com.tariqkhan051.reviewrover.models.external.GetScoresOfTeamRequest;
import com.tariqkhan051.reviewrover.models.external.GetUsersResponse;
import com.tariqkhan051.reviewrover.payload.request.GetRankingRequest;
import com.tariqkhan051.reviewrover.payload.request.UpdateReviewRequest;
import com.tariqkhan051.reviewrover.payload.response.MonthlyPendingReviewsResponse;
import com.tariqkhan051.reviewrover.payload.response.RankingResponse;
import com.tariqkhan051.reviewrover.sql.SQLCache.Queries;

public class DbHandler {

    public final String TABLE_SENSOR = "";

    // String connUrl =
    // "jdbc:postgresql://<HOSTNAME>:<PORT>/<DATABASE_NAME>?user=<USERNAME>&password=<PASSWORD>";
    // String connUrl =
    // "jdbc:postgresql://localhost:5432/example?user=postgres&password=123";
    // String connUrl = "jdbc:postgresql://host.docker.internal:5432/example?user=postgres&password=123";
    String connUrl = "jdbc:postgresql://host.docker.internal:5432/dev_test?user=postgres&password=123";
    Connection conn;

    public DbHandler() {
        try {
            conn = DriverManager.getConnection(connUrl);
            if (conn == null) {
                System.err.println("Unable to create connection.");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void Create(String[] queries) {
        try {
            for (String query : queries) {
                createSchema(conn, query);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public boolean InsertReview(Review review) {
        try {
            insertData(conn, Queries.INSERT_INTO_REVIEWS, review);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean AddUser(User user) {
        try {
            insertData(conn, Queries.INSERT_INTO_USERS, user);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return false;
    }

    public boolean UpdateUser(User user) {
        try {
            updateData(conn, Queries.UPDATE_USER, user);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean DeleteUser(User user) {
        try {
            if (deleteData(conn, Queries.DELETE_USER, user) > 0) {
                return true;
            }
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean AddTeam(Team team) {
        try {
            insertData(conn, Queries.INSERT_INTO_TEAM, team);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return false;
    }

    public boolean UpdateTeam(Team team, String name) {
        try {
            updateData(conn, Queries.UPDATE_TEAM, team, name);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean DeleteTeam(Team team) {
        try {
            if (deleteData(conn, Queries.DELETE_TEAM, team) > 0) {
                return true;
            }
            return false;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean UpdateReview(Review review) {
        try {
            updateData(conn, Queries.UPDATE_REVIEWS, review);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean UpdateReviewStatus(UpdateReviewRequest request, Integer reviewId) {
        try {
            var stmt = conn.prepareStatement(Queries.UPDATE_REVIEW_STATUS);
            stmt.setString(1, request.getStatus().toString());
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public void Insert(Boolean... cleanFlag) {
        try {
            boolean cleanTable = (cleanFlag.length >= 1) ? cleanFlag[0] : false;

            if (cleanTable) {
                truncateTable(conn, "sensors");
                truncateTable(conn, "sensor_data");
            }

            insertData(conn);

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void Execute() {
        try (var rs = executeQueries(conn, Queries.SELECT_SENSORS_DATA,
                new String[] { "temperature", "living room" })) {
            while (rs.next()) {
                System.out.printf("%s: %f%n", rs.getTimestamp(1), rs.getDouble(2));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public GetJobsResponse GetJobs() {
        GetJobsResponse response = new GetJobsResponse();
        try (var rs = executeQueries(conn, Queries.SELECT_JOBS)) {
            List<Job> jobs = new ArrayList<Job>();
            while (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt(1));
                job.setName(rs.getString(2));
                jobs.add(job);
            }
            response.setJobs(jobs);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return response;
    }

    public boolean AddJob(Job job) {
        try {
            insertData(conn, Queries.INSERT_INTO_JOB, job);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return false;
    }

    public boolean UpdateJob(Job job) {
        try {
            updateData(conn, Queries.UPDATE_JOB, job);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean DeleteJob(Job job) {
        try {
            if (deleteData(conn, Queries.DELETE_JOB, job) > 0) {
                return true;
            }
            return false;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public Score GetTotalScore(Score scoreRequest) {
        try (var stmt = conn.prepareStatement(Queries.SELECT_TOTAL_SCORE_OF_USER_FOR_MONTH_YEAR)) {
            stmt.setString(1, scoreRequest.getReview_for().getName());
            stmt.setInt(2, scoreRequest.getMonth());
            stmt.setInt(3, scoreRequest.getYear());
            var rs = stmt.executeQuery();
            while (rs.next()) {
                scoreRequest.setScore(rs.getDouble(1));
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return scoreRequest;
    }

    public Scores GetScoresOfTeam(GetScoresOfTeamRequest request) {
        Scores scores = new Scores();

        String query = "";

        var isYearProvided = request.getYear() != null;
        var isMonthProvided = request.getMonth() != null;

        if (isYearProvided && isMonthProvided) {
            query = Queries.SELECT_SCORES_OF_A_TEAM_FOR_MONTH_YEAR;
        } else if (isYearProvided) {
            query = Queries.SELECT_SCORES_OF_A_TEAM_FOR_YEAR;
        } else if (isMonthProvided) {
            query = Queries.SELECT_SCORES_OF_A_TEAM_FOR_MONTH;
        } else {
            query = Queries.SELECT_SCORES_OF_A_TEAM;
        }

        try (var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, request.getName());
            stmt.setString(2, "monthly");

            if (isYearProvided && isMonthProvided) {
                stmt.setInt(3, request.getMonth());
                stmt.setInt(4, request.getYear());
            } else if (isYearProvided) {
                stmt.setInt(3, request.getYear());
            } else if (isMonthProvided) {
                stmt.setInt(3, request.getMonth());
            }

            var rs = stmt.executeQuery();
            List<Score> _scores = new ArrayList<Score>();
            while (rs.next()) {
                Score score = new Score();
                User user = new User();
                user.setName(rs.getString(1));
                user.setTeam(new Team(rs.getString(2)));
                score.setReview_for(user);
                score.setYear(rs.getInt(3));
                score.setMonth(rs.getInt(4));
                score.setScore(rs.getDouble(5));
                _scores.add(score);
            }
            scores.setScores(_scores);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return scores;
    }

    public GetReviewsResponse GetReviewsOfTeam(GetReviewsRequest request) {
        var response = new GetReviewsResponse();

        // proc/function version
        CallableStatement callableStatement = null;
        ResultSet rs = null;
        try {
            int i = 1;
            callableStatement = conn.prepareCall("select * from select_scores_of_team(?,?,?,?,?)");
            callableStatement.setString(i++, request.getType());
            callableStatement.setString(i++, request.getTeam());
            callableStatement.setString(i++, request.getStatus());
            callableStatement.setInt(i++, request.getYear());
            callableStatement.setInt(i++, request.getMonth());

            rs = callableStatement.executeQuery();
            HashMap<Integer, List<UserScore>> monthlyReviews = new HashMap<Integer, List<UserScore>>();
            while (rs.next()) {
                UserScore user = new UserScore();
                user.setName(rs.getString(1));
                user.setScore(rs.getDouble(5));

                if (monthlyReviews.containsKey(rs.getInt(4))) {
                    monthlyReviews.get(rs.getInt(4)).add(user);
                } else {
                    List<UserScore> users = new ArrayList<UserScore>();
                    users.add(user);
                    monthlyReviews.put(rs.getInt(4), users);
                }
            }
            response.setReviews(Convert.getMonthlyReviews(monthlyReviews));
            response.setYear(request.getYear());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (callableStatement != null)
                    callableStatement.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /*
         * // query version
         * String query = "";
         * 
         * var isYearProvided = request.getYear() != null;
         * var isMonthProvided = request.getMonth() != null;
         * 
         * if (isYearProvided && isMonthProvided) {
         * query = Queries.SELECT_SCORES_OF_A_TEAM_FOR_MONTH_YEAR;
         * } else if (isYearProvided) {
         * query = Queries.SELECT_SCORES_OF_A_TEAM_FOR_YEAR;
         * } else if (isMonthProvided) {
         * query = Queries.SELECT_SCORES_OF_A_TEAM_FOR_MONTH;
         * } else {
         * query = Queries.SELECT_SCORES_OF_A_TEAM;
         * }
         * 
         * try (var stmt = conn.prepareStatement(query)) {
         * 
         * stmt.setString(1, request.getTeam());
         * stmt.setString(2, "monthly");
         * 
         * if (isYearProvided && isMonthProvided) {
         * stmt.setInt(3, request.getMonth());
         * stmt.setInt(4, request.getYear());
         * } else if (isYearProvided) {
         * stmt.setInt(3, request.getYear());
         * } else if (isMonthProvided) {
         * stmt.setInt(3, request.getMonth());
         * }
         * 
         * var rs = stmt.executeQuery();
         * HashMap<Integer, List<UserScore>> monthlyReviews = new HashMap<Integer,
         * List<UserScore>>();
         * 
         * while (rs.next()) {
         * UserScore user = new UserScore();
         * user.setName(rs.getString(1));
         * user.setScore(rs.getDouble(5));
         * 
         * if (monthlyReviews.containsKey(rs.getInt(4))) {
         * monthlyReviews.get(rs.getInt(4)).add(user);
         * } else {
         * List<UserScore> users = new ArrayList<UserScore>();
         * users.add(user);
         * monthlyReviews.put(rs.getInt(4), users);
         * }
         * }
         * response.setReviews(Convert.getMonthlyReviews(monthlyReviews));
         * response.setYear(request.getYear());
         * 
         * } catch (SQLException ex) {
         * System.err.println(ex.getMessage());
         * }
         * 
         */

        return response;
    }

    public List<RankingResponse> GetRankingOfAllUsers(GetRankingRequest request) {
        List<RankingResponse> response = new ArrayList<RankingResponse>();
        CallableStatement callableStatement = null;
        ResultSet rs = null;
        String reviewType = "";
        if (request.getReview_type() != null)
        {
            reviewType = request.getReview_type().toString();
        }
        try {
            int i = 1;
            callableStatement = conn.prepareCall("select * from select_scores_of_all_users(?,?,?,?)");
            callableStatement.setString(i++, reviewType);
            callableStatement.setString(i++, EReviewStatus.APPROVED.toString());
            callableStatement.setInt(i++, request.getYear());
            callableStatement.setInt(i++, request.getMonth());

            rs = callableStatement.executeQuery();
            int rank = 1;
            while (rs.next()) {
                var user_name = rs.getString(1);
                var team_name = rs.getString(2);
                // var review_year = rs.getInt(3);
                // var review_month = rs.getInt(4);
                var avg_score = rs.getDouble(5);
                // var review_good_quality = rs.getString(6);
                // var review_bad_quality = rs.getString(7);

                RankingResponse ranking = new RankingResponse();
                ranking.setRank(rank++);
                ranking.setName(user_name);
                ranking.setTeam_name(team_name);
                ranking.setScore(avg_score);
                response.add(ranking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (callableStatement != null)
                    callableStatement.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return response;
    }

    public GetReviewsResponse GetReviewsOfAllTeams(GetReviewsRequest request) {
        GetReviewsResponse response = new GetReviewsResponse();

        // proc version
        CallableStatement callableStatement = null;
        ResultSet rs = null;
        try {
            int i = 1;
            callableStatement = conn.prepareCall("select * from select_scores_of_all_teams(?,?,?,?)");
            callableStatement.setString(i++, request.getStatus());
            callableStatement.setString(i++, request.getType());
            callableStatement.setInt(i++, request.getYear());
            callableStatement.setInt(i++, request.getMonth());

            rs = callableStatement.executeQuery();
            HashMap<Integer, List<TeamScore>> monthlyReviews = new HashMap<Integer, List<TeamScore>>();
            while (rs.next()) {
                TeamScore team = new TeamScore();
                team.setName(rs.getString(1));
                team.setScore(rs.getDouble(4));
                var year = rs.getInt(2);
                var month = rs.getInt(3);

                if (monthlyReviews.containsKey(month)) {
                    monthlyReviews.get(month).add(team);
                } else {
                    List<TeamScore> teams = new ArrayList<TeamScore>();
                    teams.add(team);
                    monthlyReviews.put(month, teams);
                }
            }
            response.setReviews(Convert.getMonthlyReviewsOfTeams(monthlyReviews));
            response.setYear(request.getYear());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (callableStatement != null)
                    callableStatement.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /*
         * Query version
         * 
         * 
         * 
         * String query = "";
         * 
         * var isYearProvided = request.getYear() != null;
         * var isMonthProvided = request.getMonth() != null;
         * 
         * if (isYearProvided && isMonthProvided) {
         * query = Queries.SELECT_SCORES_OF_ALL_TEAMS_FOR_YEAR_MONTH;
         * } else if (isYearProvided) {
         * query = Queries.SELECT_SCORES_OF_ALL_TEAMS_FOR_YEAR;
         * } else if (isMonthProvided) {
         * query = Queries.SELECT_SCORES_OF_ALL_TEAMS_FOR_MONTH;
         * } else {
         * query = Queries.SELECT_SCORES_OF_ALL_TEAMS;
         * }
         * 
         * try (var stmt = conn.prepareStatement(query)) {
         * 
         * stmt.setString(1, "monthly");
         * 
         * if (isYearProvided && isMonthProvided) {
         * stmt.setInt(2, request.getMonth());
         * stmt.setInt(3, request.getYear());
         * } else if (isYearProvided) {
         * stmt.setInt(2, request.getYear());
         * } else if (isMonthProvided) {
         * stmt.setInt(2, request.getMonth());
         * }
         * 
         * var rs = stmt.executeQuery();
         * HashMap<Integer, List<TeamScore>> monthlyReviews = new HashMap<Integer,
         * List<TeamScore>>();
         * 
         * while (rs.next()) {
         * TeamScore team = new TeamScore();
         * team.setName(rs.getString(1));
         * team.setScore(rs.getDouble(5));
         * 
         * if (monthlyReviews.containsKey(rs.getInt(4))) {
         * monthlyReviews.get(rs.getInt(4)).add(team);
         * } else {
         * List<TeamScore> teams = new ArrayList<TeamScore>();
         * teams.add(team);
         * monthlyReviews.put(rs.getInt(4), teams);
         * }
         * }
         * response.setReviews(Convert.getMonthlyReviewsOfTeams(monthlyReviews));
         * response.setYear(request.getYear());
         * 
         * } catch (SQLException ex) {
         * System.err.println(ex.getMessage());
         * }
         */

        return response;
    }

    public GetReviewsResponse GetReviewsOfAllDepartments(GetReviewsRequest request) {
        GetReviewsResponse response = new GetReviewsResponse();

        // proc version
        CallableStatement callableStatement = null;
        ResultSet rs = null;
        try {
            int i = 1;
            callableStatement = conn.prepareCall("select * from select_scores_of_all_departments(?,?,?,?)");
            callableStatement.setString(i++, request.getStatus());
            callableStatement.setString(i++, request.getType());
            callableStatement.setInt(i++, request.getYear());
            callableStatement.setInt(i++, request.getMonth());

            rs = callableStatement.executeQuery();
            HashMap<Integer, List<DepartmentScore>> monthlyReviews = new HashMap<Integer, List<DepartmentScore>>();
            while (rs.next()) {
                DepartmentScore department = new DepartmentScore();
                department.setName(rs.getString(1));
                department.setScore(rs.getDouble(4));
                var year = rs.getInt(2);
                var month = rs.getInt(3);

                if (monthlyReviews.containsKey(month)) {
                    monthlyReviews.get(month).add(department);
                } else {
                    List<DepartmentScore> departments = new ArrayList<DepartmentScore>();
                    departments.add(department);
                    monthlyReviews.put(month, departments);
                }
            }
            response.setReviews(Convert.getMonthlyReviewsOfDepartments(monthlyReviews));
            response.setYear(request.getYear());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (callableStatement != null)
                    callableStatement.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return response;
    }

    public GetReviewsResponse GetReviewsOfUser(GetReviewsRequest request) {
        var response = new GetReviewsResponse();

        // proc/function version
        CallableStatement callableStatement = null;
        ResultSet rs = null;
        try {
            int i = 1;
            callableStatement = conn.prepareCall("select * from select_review_of_user(?,?,?,?,?,?)");
            callableStatement.setString(i++, request.getUser());
            callableStatement.setInt(i++, request.getMonth());
            callableStatement.setInt(i++, request.getYear());
            callableStatement.setString(i++, request.getStatus());
            callableStatement.setString(i++, request.getType());
            callableStatement.setString(i++, request.getTeam());
            rs = callableStatement.executeQuery();
            List<MonthlyReview> reviews = new ArrayList<MonthlyReview>();
            while (rs.next()) {
                int index = 1;
                MonthlyReview review = new MonthlyReview();
                review.setScore(rs.getDouble(index++));
                review.setGood_quality(rs.getString(index++));
                review.setBad_quality(rs.getString(index++));
                review.setMonth(rs.getInt(index++));
                reviews.add(review);
            }
            response.setReviews(reviews);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (callableStatement != null)
                    callableStatement.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        /*
         * // Query Version
         * 
         * var isYearProvided = request.getYear() != null;
         * var isMonthProvided = request.getMonth() != null;
         * var query = Queries.SELECT_REVIEW_OF_USER;
         * 
         * if (isMonthProvided && isYearProvided) {
         * query = Queries.SELECT_REVIEW_OF_USER_FOR_MONTH_YEAR;
         * } else if (isMonthProvided) {
         * query = Queries.SELECT_REVIEW_OF_USER_FOR_MONTH;
         * } else if (isYearProvided) {
         * query = Queries.SELECT_REVIEW_OF_USER_FOR_YEAR;
         * }
         * 
         * try (var stmt = conn.prepareStatement(query)) {
         * int i = 1;
         * stmt.setString(i++, request.getUser());
         * 
         * if (isMonthProvided) {
         * stmt.setInt(i++, request.getMonth());
         * }
         * 
         * if (isYearProvided) {
         * stmt.setInt(i++, request.getYear());
         * }
         * 
         * stmt.setString(i++, request.getType());
         * stmt.setString(i++, "approved");
         * stmt.setString(i++, request.getTeam());
         * 
         * var rs = stmt.executeQuery();
         * 
         * List<MonthlyReview> reviews = new ArrayList<MonthlyReview>();
         * while (rs.next()) {
         * MonthlyReview review = new MonthlyReview();
         * review.setScore(rs.getDouble(1));
         * review.setGood_quality(rs.getString(2));
         * review.setBad_quality(rs.getString(3));
         * review.setMonth(rs.getInt(4));
         * reviews.add(review);
         * }
         * response.setReviews(reviews);
         * 
         * } catch (SQLException ex) {
         * System.err.println(ex.getMessage());
         * }
         * 
         */

        return response;
    }

    public MonthlyPendingReviewsResponse GetPendingReviews(GetReviewsRequest request, boolean isAdmin) {
        MonthlyPendingReviewsResponse response = new MonthlyPendingReviewsResponse();

        if (isAdmin) {
            try (var stmt = conn.prepareStatement(Queries.SELECT_PENDING_REVIEWS_FOR_ADMIN)) {
                stmt.setString(1, request.getType());
                stmt.setString(2, "PENDING");
                stmt.setString(3, request.getTeam());
                var rs = stmt.executeQuery();

                List<PendingReview> pendingReviews = new ArrayList<PendingReview>();
                while (rs.next()) {
                    PendingReview pendingReview = new PendingReview();
                    pendingReview.setId(rs.getInt(1));
                    pendingReview.setUser(rs.getString(2));
                    pendingReview.setTitle(rs.getString(3));
                    pendingReview.setDescription(rs.getString(4));
                    pendingReview.setCreated_on(rs.getDate(5));
                    pendingReviews.add(pendingReview);
                }
                response.setReviews(pendingReviews);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            try (var stmt = conn.prepareStatement(Queries.SELECT_PENDING_REVIEWS)) {
                stmt.setString(1, request.getUser());
                stmt.setInt(2, request.getMonth());
                stmt.setInt(3, request.getYear());
                stmt.setString(4, request.getType());
                stmt.setString(5, "PENDING");
                stmt.setString(6, request.getTeam());
                var rs = stmt.executeQuery();

                List<PendingReview> pendingReviews = new ArrayList<PendingReview>();
                while (rs.next()) {
                    PendingReview pendingReview = new PendingReview();
                    pendingReview.setId(rs.getInt(1));
                    pendingReview.setUser(rs.getString(2));
                    pendingReview.setTitle(rs.getString(3));
                    pendingReview.setDescription(rs.getString(4));
                    pendingReview.setCreated_on(rs.getDate(5));
                    pendingReviews.add(pendingReview);
                }
                response.setReviews(pendingReviews);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        return response;
    }

    public MonthlyPendingReviewsResponse GetReviews(GetReviewsRequest request) {
        MonthlyPendingReviewsResponse response = new MonthlyPendingReviewsResponse();
        try (var stmt = conn.prepareStatement(Queries.SELECT_ALL_PENDING_REVIEWS_OF_USER_FOR_MONTH_YEAR)) {
            int index = 1;
            stmt.setInt(index++, request.getMonth());
            stmt.setInt(index++, request.getYear());
            stmt.setString(index++, request.getUser());
            var rs = stmt.executeQuery();

            List<PendingReview> pendingReviews = new ArrayList<PendingReview>();
            while (rs.next()) {
                PendingReview pendingReview = new PendingReview();
                pendingReview.setUser(rs.getString(1));
                pendingReview.setUsername(rs.getString(2));
                pendingReview.setJob_name(rs.getString(3));
                pendingReviews.add(pendingReview);
            }
            response.setReviews(pendingReviews);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return response;
    }

    public List<Team> GetTeams() {
        List<Team> teams = new ArrayList<>();
        try (var rs = executeQueries(conn,
                Queries.SELECT_TEAM)) {
            while (rs.next()) {
                Team t = new Team();
                t.setId(rs.getInt(1));
                t.setName(rs.getString(2));

                teams.add(t);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return teams;
    }

    public GetUsersResponse GetUsers(String team_name) throws SQLException {
        GetUsersResponse response = new GetUsersResponse();
        List<User> users = new ArrayList<>();
        if (Utils.IsNullOrEmpty(team_name)) {
            try (var stmt = conn.prepareStatement(Queries.SELECT_USERS)) {
                var rs = stmt.executeQuery();
                while (rs.next()) {
                    User u = new User();
                    int index = 1;
                    u.setUsername(rs.getString(index++));
                    u.setName(rs.getString(index++));
                    u.setManager(new User(rs.getString(index++)));
                    //u.setJob(new Job(rs.getString(index++)));
                    u.setTeam(new Team(rs.getString(index++)));
                    users.add(u);
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            try (var stmt = conn.prepareStatement(Queries.SELECT_USERS_FOR_TEAM)) {
                stmt.setString(1, team_name);
                var rs = stmt.executeQuery();
                while (rs.next()) {
                    User u = new User();
                    int index = 1;
                    u.setUsername(rs.getString(index++));
                    u.setName(rs.getString(index++));
                    u.setManager(new User(rs.getString(index++)));
                    //u.setJob(new Job(rs.getString(index++)));
                    u.setTeam(new Team(rs.getString(index++)));
                    users.add(u);
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                throw ex;
            }
        }

        response.setUsers(users);

        return response;
    }

    public boolean GetUserByNameAndPassword(User user) {
        try {
            var stmt = conn.prepareStatement(Queries.SELECT_USER_BY_USERNAME_AND_PASSWORD);
            Integer index = 1;
            stmt.setString(index++, user.getUsername());
            stmt.setString(index++, user.getPassword());
            var rs = stmt.executeQuery();

            while (rs.next()) {
                user.setId(rs.getLong(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return user.getId() > 0;
    }

    private void createSchema(final Connection conn, String query) throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute(query);
        }
    }

    private void insertData(final Connection conn, String query, Review review) throws SQLException {
        var stmt = conn.prepareStatement(query);
        int index = 1;

        stmt.setString(index++, review.getSubmittedBy().getName());
        stmt.setString(index++, review.getReviewFor().getName());
        stmt.setDouble(index++, review.getScore());
        stmt.setString(index++, review.getGoodQuality());
        stmt.setString(index++, review.getBadQuality());
        stmt.setInt(index++, review.getMonth());
        stmt.setInt(index++, review.getYear());
        stmt.setString(index++, review.getReviewType().getName().toString());
        stmt.setString(index++, 
                review.getReviewType().getName().toString().equalsIgnoreCase("random") ? "pending" : "approved");
        stmt.setString(index++, review.getTitle());
        stmt.setString(index++, review.getDescription());
        stmt.setString(index++, review.getReviewFor().getName());
        stmt.setString(index++, review.getSubmittedBy().getName());
        stmt.setInt(index++, review.getMonth());
        stmt.setInt(index++, review.getYear());

        stmt.executeUpdate();
    }

    private void insertData(final Connection conn, String query, User user) throws SQLException {
        try (var stmt = conn.prepareStatement(query)) {
            int index = 1;
            stmt.setString(index++, user.getName());
            stmt.setString(index++, user.getUsername());
            stmt.setString(index++, user.getNickName());
            stmt.setString(index++, user.getEmail());
            stmt.setString(index++, user.getPassword());
            stmt.setString(index++, user.getTeam().getName());
            stmt.setString(index++, user.getManager().getName());
            stmt.setString(index++, user.getJob().getName());
            stmt.setString(index++, user.getUsername());
            stmt.executeUpdate();
        }
    }

    private void insertData(final Connection conn, String query, Team team) throws SQLException {
        try (var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, team.getName());
            stmt.executeUpdate();
        }
    }

    private void insertData(final Connection conn, String query, Job job) throws SQLException {
        try (var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, job.getName());
            stmt.executeUpdate();
        }
    }

    private void updateData(final Connection conn, String query, Review review) throws SQLException {
        var stmt = conn.prepareStatement(query);
        stmt.setDouble(1, review.getScore());
        stmt.setString(2, review.getGoodQuality());
        stmt.setString(3, review.getBadQuality());
        stmt.setString(4, review.getSubmittedBy().getName());
        stmt.setString(5, review.getReviewFor().getName());
        stmt.setInt(6, review.getMonth());
        stmt.setInt(7, review.getYear());
        stmt.setString(8, review.getReviewType().getName().toString());
        stmt.executeUpdate();
    }

    private void updateData(final Connection conn, String query, User user) throws SQLException {
        var stmt = conn.prepareStatement(query);
        int index = 1;
        stmt.setString(index++, user.getName());
        stmt.setString(index++, user.getNickName());
        stmt.setString(index++, user.getEmail());
        stmt.setString(index++, user.getPassword());
        stmt.setString(index++, user.getTeam().getName());
        stmt.setString(index++, user.getManager().getName());
        stmt.setString(index++, user.getJob().getName());
        stmt.setString(index++, user.getUsername());
        stmt.executeUpdate();
    }

    private void updateData(final Connection conn, String query, Team team, String name) throws SQLException {
        var stmt = conn.prepareStatement(query);
        stmt.setString(1, team.getName());
        stmt.setString(2, name);
        stmt.executeUpdate();
    }

    private void updateData(final Connection conn, String query, Job job) throws SQLException {
        var stmt = conn.prepareStatement(query);
        stmt.setString(1, job.getName());
        stmt.setInt(2, job.getId());
        stmt.executeUpdate();
    }

    private int deleteData(final Connection conn, String query, Team team) throws SQLException {
        var stmt = conn.prepareStatement(query);
        stmt.setString(1, team.getName());
        return stmt.executeUpdate();
    }

    private int deleteData(final Connection conn, String query, Job job) throws SQLException {
        var stmt = conn.prepareStatement(query);
        stmt.setInt(1, job.getId());
        return stmt.executeUpdate();
    }

    private int deleteData(final Connection conn, String query, User user) throws SQLException {
        var stmt = conn.prepareStatement(query);
        int index = 1;
        stmt.setString(index++, user.getUsername());
        stmt.setString(index++, user.getUsername());
        stmt.setString(index++, user.getUsername());
        return stmt.executeUpdate();
    }

    private void insertData(final Connection conn) throws SQLException {
        final List<Sensor> sensors = List.of(
                new Sensor("temperature", "bedroom"),
                new Sensor("temperature", "living room"),
                new Sensor("temperature", "outside"),
                new Sensor("humidity", "kitchen"),
                new Sensor("humidity", "outside"));
        for (final var sensor : sensors) {
            try (var stmt = conn.prepareStatement("INSERT INTO sensors (type, location) VALUES (?, ?)")) {
                stmt.setString(1, sensor.type());
                stmt.setString(2, sensor.location());
                stmt.executeUpdate();
            }
        }

        final var sensorDataCount = 100;
        final var insertBatchSize = 10;
        try (var stmt = conn.prepareStatement("""
                INSERT INTO sensor_data (time, sensor_id, value)
                VALUES (
                    generate_series(now() - INTERVAL '24 hours', now(), INTERVAL '5 minutes'),
                    floor(random() * 4 + 1)::INTEGER,
                    random()
                )
                """)) {
            for (int i = 0; i < sensorDataCount; i++) {
                stmt.addBatch();

                if ((i > 0 && i % insertBatchSize == 0) || i == sensorDataCount - 1) {
                    stmt.executeBatch();
                }
            }
        }
    }

    private ResultSet executeQueries(final Connection conn, String query, String[] args) throws SQLException {
        var stmt = conn.prepareStatement(query);
        Integer index = 1;
        for (String arg : args) {
            stmt.setString(index, arg);
            index++;
        }
        return stmt.executeQuery();
    }

    private ResultSet executeQueries(final Connection conn, String query) throws SQLException {
        var stmt = conn.prepareStatement(query);
        return stmt.executeQuery();
    }

    private void truncateTable(final Connection conn, String tableName) throws SQLException {
        try (var stmt = conn.prepareStatement("TRUNCATE TABLE " + tableName + " CASCADE;")) {
            stmt.executeUpdate();
        }
    }

    private record Sensor(String type, String location) {

    }
}