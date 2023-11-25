package com.tariqkhan051.reviewrover.sql;

public class SQLCache {

        public static class Queries {

                // #region SENSOR QUERIES
                public static String CREATE_TABLE_SENSOR = """
                                CREATE TABLE IF NOT EXISTS sensors (
                                    id SERIAL PRIMARY KEY,
                                    type TEXT NOT NULL,
                                    location TEXT NOT NULL
                                )
                                """;
                public static String CREATE_TABLE_SENSOR_DATA = """
                                CREATE TABLE sensor_data (
                                    time TIMESTAMPTZ NOT NULL,
                                    sensor_id INTEGER REFERENCES sensors (id),
                                    value DOUBLE PRECISION
                                )
                                """;
                public static String CREATE_HYPER_TABLE_SENSOR_DATA = "SELECT create_hypertable('sensor_data', 'time')";

                // #endregion

                // #region CREATE TABLES
                public static String CREATE_TABLE_USERS = """
                                CREATE TABLE IF NOT EXISTS USERS
                                (
                                    id SERIAL PRIMARY KEY,
                                    username text UNIQUE NOT NULL,
                                    name varchar(50) NOT NULL,
                                    nick_name varchar(50),
                                    email varchar(255),
                                    password varchar(255),
                                    team_id integer NOT NULL,
                                    manager_id integer,
                                    job_id integer,
                                    is_live boolean,
                                    created_on timestamp DEFAULT CURRENT_DATE,
                                    last_login timestamp DEFAULT CURRENT_DATE,

                                    CONSTRAINT fk_users_teams FOREIGN KEY (team_id) REFERENCES TEAMS (id),
                                    CONSTRAINT fk_users_managers FOREIGN KEY (manager_id) REFERENCES USERS (id),
                                    CONSTRAINT fk_users_jobs FOREIGN KEY (job_id) REFERENCES JOBS (id)
                                )
                                """;

                public static String CREATE_TABLE_TEAMS = """
                                CREATE TABLE IF NOT EXISTS TEAMS
                                (
                                    id SERIAL PRIMARY KEY,
                                    name varchar(100) NOT NULL UNIQUE,
                                    created_on timestamp NOT NULL
                                )
                                """;

                public static String CREATE_TABLE_JOBS = """
                                CREATE TABLE IF NOT EXISTS JOBS
                                (
                                    id SERIAL PRIMARY KEY,
                                    name varchar(100) NOT NULL UNIQUE,
                                    created_on timestamp NOT NULL
                                )
                                """;

                public static String CREATE_TABLE_REVIEWS = """
                                CREATE TABLE IF NOT EXISTS REVIEWS
                                (
                                    id SERIAL PRIMARY KEY,
                                    score decimal NOT NULL,
                                    submitted_by integer NOT NULL,
                                    review_for integer NOT NULL,
                                	good_quality TEXT,
                                	bad_quality TEXT,
                                	month integer NOT NULL,
                                	year integer NOT NULL,
                                    review_type_id integer NOT NULL,
                                    created_on timestamp NOT NULL DEFAULT CURRENT_DATE,
                                    status TEXT,
                                    title TEXT,
                                    description TEXT,
                                    CONSTRAINT fk_submitted_by FOREIGN KEY (submitted_by)
                                        REFERENCES USERS (id),
                                    CONSTRAINT fk_review_for FOREIGN KEY (review_for)
                                        REFERENCES USERS (id),
                                    CONSTRAINT fk_review_type FOREIGN KEY (review_type_id)
                                        REFERENCES REVIEW_TYPE (id)
                                )
                                """;

                public static String CREATE_TABLE_REVIEW_TYPE = """
                                CREATE TABLE IF NOT EXISTS REVIEW_TYPE
                                (
                                	id SERIAL PRIMARY KEY,
                                	name TEXT NOT NULL
                                )
                                """;

                // #endregion

                // #region INSERT QUERIES
                public static String INSERT_INTO_TEAM = """
                                INSERT INTO TEAMS (name, created_on) VALUES (?, CURRENT_TIMESTAMP) ON CONFLICT DO NOTHING
                                """;

                public static String INSERT_INTO_JOB = """
                                INSERT INTO JOBS (name, created_on) VALUES (?, CURRENT_TIMESTAMP) ON CONFLICT DO NOTHING
                                """;

                public static String INSERT_INTO_USERS = """
                                INSERT INTO USERS
                                (name, username, nick_name, email, password,
                                team_id, manager_id, job_id, is_live, created_on)
                                SELECT ?, ?, ?, ?, ?,
                                (SELECT id FROM TEAMS WHERE name = ? limit 1),
                                (SELECT id FROM USERS WHERE username = ? limit 1),
                                (SELECT id FROM JOBS WHERE name = ? limit 1),
                                '1', CURRENT_TIMESTAMP
                                WHERE NOT EXISTS (SELECT id FROM USERS WHERE username = ?)
                                """;

                public static String INSERT_INTO_REVIEWS = """
                                INSERT INTO REVIEWS (submitted_by, review_for, score, good_quality, bad_quality,
                                        month, year, review_type_id, created_on, status, title, description)
                                        SELECT
                                        (SELECT id from USERS WHERE username = ?),
                                        (SELECT id from USERS WHERE username = ?),
                                        ?, ?, ?, ?, ?,
                                        (SELECT id from REVIEW_TYPE WHERE name = ?),
                                        CURRENT_TIMESTAMP,
                                        ?, ?, ? WHERE NOT EXISTS
                                        (SELECT id from REVIEWS WHERE
                                         review_for = (select id from USERS where username = ?) and
                                         submitted_by = (select id from USERS where username = ?) and
                                         month = ? and
                                         year = ?)
                                """;
                // #endregion

                // #region SELECT QUERIES
                public static String SELECT_SENSORS_DATA = """
                                SELECT time_bucket('15 minutes', time) AS bucket, avg(value)
                                FROM sensor_data
                                JOIN sensors ON sensors.id = sensor_data.sensor_id
                                WHERE sensors.type = ? AND sensors.location = ?
                                GROUP BY bucket
                                ORDER BY bucket DESC
                                """;

                public static String SELECT_TOTAL_SCORE_OF_USER_FOR_MONTH_YEAR = """
                                SELECT round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2) from REVIEWS r
                                INNER JOIN users u ON
                                r.review_for = u.id
                                WHERE r.review_for = (SELECT id from USERS WHERE username = ? limit 1) and
                                month = ? and year = ?
                                """;

                public static String SELECT_REVIEW_OF_USER_FOR_MONTH_YEAR = """
                                SELECT round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2),
                                STRING_AGG(r.good_quality, ', '), STRING_AGG(r.bad_quality, ', '),
                                r.month
                                FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                WHERE r.review_for = (SELECT id FROM USERS WHERE username = ? limit 1) and
                                month = ? and
                                year = ? and
                                review_type_id = (SELECT id FROM REVIEW_TYPE WHERE name = ?) and
                                status = ? and
                                team_id = (SELECT id FROM TEAMS WHERE name = ? limit 1)
                                GROUP BY r.month
                                """;

                public static String SELECT_REVIEW_OF_USER_FOR_YEAR = """
                                SELECT round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2),
                                STRING_AGG(r.good_quality, ', '), STRING_AGG(r.bad_quality, ', '),
                                r.month
                                FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                WHERE r.review_for = (SELECT id FROM USERS WHERE username = ? limit 1) and
                                year = ? and
                                review_type_id = (SELECT id FROM REVIEW_TYPE WHERE name = ?) and
                                status = ? and
                                team_id = (SELECT id FROM TEAMS WHERE name = ? limit 1)
                                GROUP BY r.month
                                """;

                public static String SELECT_REVIEW_OF_USER_FOR_MONTH = """
                                SELECT round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2),
                                STRING_AGG(r.good_quality, ', '), STRING_AGG(r.bad_quality, ', '),
                                r.month
                                FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                WHERE r.review_for = (SELECT id FROM USERS WHERE username = ? limit 1) and
                                month = ? and
                                review_type_id = (SELECT id FROM REVIEW_TYPE WHERE name = ?) and
                                status = ? and
                                team_id = (SELECT id FROM TEAMS WHERE name = ? limit 1)
                                GROUP BY r.month
                                """;

                public static String SELECT_REVIEW_OF_USER = """
                                SELECT round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2),
                                STRING_AGG(r.good_quality, ', '), STRING_AGG(r.bad_quality, ', '),
                                r.month
                                FROM REVIEWS r
                                INNER JOIN USERS u ON
                                r.review_for = u.id
                                WHERE r.review_for = (SELECT id FROM USERS WHERE username = ? limit 1) and
                                review_type_id = (SELECT id FROM REVIEW_TYPE WHERE name = ?) and
                                status = ? and
                                team_id = (SELECT id FROM TEAMS WHERE name = ? limit 1)
                                GROUP BY r.month
                                """;

                public static String SELECT_ALL_PENDING_REVIEWS_OF_USER_FOR_MONTH_YEAR = """
                                SELECT u2.name, u2.username, j.name, r.year, r.month, u1.name as submitted_by, u2.name as review_for, r.score
                                FROM USERS u1
                                LEFT OUTER JOIN USERS u2
                                ON u1.name != u2.name
                                LEFT OUTER JOIN JOBS j
                                ON j.id = u2.job_id
                                LEFT OUTER JOIN REVIEWS r
                                ON r.submitted_by = u1.id and r.review_for = u2.id
                                WHERE (r.month = ? or r.month is null) and (r.year = ? or r.year is null) and
                                (r.review_type_id = (select id FROM review_type WHERE name = 'MONTHLY') or
                                r.review_type_id is null)
                                and r.score is null and u1.username = ? and u2.team_id = u1.team_id and 
                                (u1.is_live = true or u1.is_live is null) and
                                (u2.is_live = true or u2.is_live is null)
                                """;

                public static String SELECT_PENDING_REVIEWS = """
                                SELECT r.id, u.name, r.title, r.description, r.created_on
                                FROM REVIEWS r
                                INNER JOIN USERS u ON
                                r.review_for = u.id
                                WHERE r.review_for = (SELECT id FROM USERS WHERE username = ? limit 1) and
                                month = ? and
                                year = ? and
                                review_type_id = (SELECT id FROM REVIEW_TYPE WHERE name = ?) and
                                review_status_id = (SELECT id FROM REVIEW_STATUS where name = ?) and
                                team_id = (SELECT id FROM TEAMS WHERE name = ? limit 1)
                                """;

                public static String SELECT_PENDING_REVIEWS_FOR_ADMIN = """
                                SELECT r.id, u.name, r.title, r.description, r.created_on
                                FROM REVIEWS r
                                INNER JOIN USERS u ON
                                r.review_for = u.id
                                WHERE review_type_id = (SELECT id FROM REVIEW_TYPE WHERE name = ?) and
                                review_status_id = (SELECT id FROM REVIEW_STATUS where name = ?) and
                                team_id = (SELECT id FROM TEAMS WHERE name = ? limit 1)
                                """;

                public static String SELECT_SCORES_OF_A_TEAM = """
                                SELECT u.name, t.name, r.year, r.month, round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2),
                                STRING_AGG(r.good_quality, ', '),  STRING_AGG(r.bad_quality, ', ') FROM REVIEWS r
                                INNER JOIN users u ON r.review_for = u.id
                                LEFT JOIN teams t on t.id = u.team_id
                                WHERE t.name = ? and r.status = 'approved' and
                                r.review_type_id = (select id FROM REVIEW_TYPE WHERE name = ?)
                                GROUP BY t.name, u.name, r.year, r.month
                                """;

                public static String SELECT_SCORES_OF_ALL_TEAMS = """
                                SELECT t.name, r.year, r.month, round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2)
                                FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                LEFT JOIN TEAMS t on t.id = u.team_id
                                WHERE r.status = 'approved' and
                                r.review_type_id = (SELECT id FROM REVIEW_TYPE WHERE name = ?)
                                GROUP BY t.name, r.year, r.month
                                """;

                public static String SELECT_SCORES_OF_ALL_TEAMS_FOR_YEAR = """
                                SELECT t.name, r.year, r.month, round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2)
                                FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                LEFT JOIN TEAMS t on t.id = u.team_id
                                WHERE r.status = 'approved' and
                                r.review_type_id = (SELECT id FROM REVIEW_TYPE WHERE name = ?) and
                                r.year = ?
                                GROUP BY t.name, r.year, r.month
                                """;

                public static String SELECT_SCORES_OF_ALL_TEAMS_FOR_MONTH = """
                                SELECT t.name, r.year, r.month, round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2)
                                FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                LEFT JOIN TEAMS t on t.id = u.team_id
                                WHERE r.status = 'approved' and
                                r.review_type_id = (SELECT id from REVIEW_TYPE WHERE name = ?) and
                                r.month = ?
                                GROUP BY t.name, r.year, r.month
                                """;

                public static String SELECT_SCORES_OF_ALL_TEAMS_FOR_YEAR_MONTH = """
                                SELECT t.name, r.year, r.month, round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2)
                                FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                LEFT JOIN TEAMS t on t.id = u.team_id
                                WHERE r.status = 'approved' and
                                r.review_type_id = (SELECT id from REVIEW_TYPE WHERE name = ?) and
                                r.month = ? and r.year = ?
                                GROUP BY t.name, r.year, r.month
                                """;

                public static String SELECT_SCORES_OF_A_TEAM_FOR_YEAR = """
                                SELECT u.name, t.name, r.year, r.month, round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2),
                                STRING_AGG(r.good_quality, ', '),  STRING_AGG(r.bad_quality, ', ') FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                LEFT JOIN TEAMS t on t.id = u.team_id
                                WHERE t.name = ? and r.status = 'approved' and
                                r.review_type_id = (SELECT id FROM REVIEW_TYPE WHERE name = ?) and
                                r.year = ?
                                GROUP BY t.name, u.name, r.year, r.month
                                """;

                public static String SELECT_SCORES_OF_A_TEAM_FOR_MONTH = """
                                SELECT u.name, t.name, r.year, r.month, round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2),
                                STRING_AGG(r.good_quality, ', '),  STRING_AGG(r.bad_quality, ', ') FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                LEFT JOIN TEAMS t on t.id = u.team_id
                                WHERE t.name = ? and r.status = 'approved' and
                                r.review_type_id = (select id FROM REVIEW_TYPE WHERE name = ?) and
                                r.month = ?
                                GROUP BY t.name, u.name, r.year, r.month
                                """;

                public static String SELECT_SCORES_OF_A_TEAM_FOR_MONTH_YEAR = """
                                SELECT u.name, t.name, r.year, r.month, round(((100.0 * sum(r.score)) / (count(r.id) * 5)), 2),
                                STRING_AGG(r.good_quality, ', '),  STRING_AGG(r.bad_quality, ', ') FROM REVIEWS r
                                INNER JOIN USERS u ON r.review_for = u.id
                                LEFT JOIN TEAMS t ON t.id = u.team_id
                                WHERE t.name = ? and r.status = 'approved' and
                                r.review_type_id = (SELECT id FROM REVIEW_TYPE where name = ?) and
                                r. month = ? and
                                r.year = ?
                                GROUP BY t.name, u.name, r.year, r.month
                                """;

                public static String SELECT_TEAM = """
                                SELECT * from TEAMS
                                """;

                public static String SELECT_USER_BY_USERNAME_AND_PASSWORD = """
                                SELECT id from USERS WHERE username = ? and password = ? limit 1
                                """;

                public static String SELECT_USERS = """
                                SELECT u.username, u.name, m.name, j.name, t.name
                                from USERS u
                                LEFT OUTER JOIN TEAMS t on t.id = u.team_id
                                LEFT OUTER JOIN USERS m on m.id = u.manager_id
                                LEFT OUTER JOIN JOBS j on j.id = u.job_id
                                """;

                public static String SELECT_USERS_FOR_TEAM = """
                                SELECT u.username, u.name, m.name, j.name, t.name
                                FROM USERS u
                                LEFT OUTER JOIN TEAMS t ON t.id = u.team_id
                                LEFT OUTER JOIN USERS m ON m.id = u.manager_id
                                LEFT OUTER JOIN JOBS j ON j.id = u.job_id
                                WHERE u.team_id = (SELECT id FROM TEAMS WHERE name = ? limit 1)
                                """;

                public static String SELECT_JOBS = """
                                SELECT id, name FROM jobs
                                """;

                // #endregion

                // #region Update records
                public static String UPDATE_REVIEWS = """
                                UPDATE REVIEWS SET score = ?, good_quality = ?, bad_quality = ?
                                WHERE id =
                                (
                                    SELECT id from REVIEWS where
                                    submitted_by = (SELECT id from USERS where username = ?) and
                                    review_for = (SELECT id from USERS where username = ?) and
                                    month = ? and year = ? and
                                    review_type_id = (SELECT id from REVIEW_TYPE where name = ?)
                                    limit 1
                                )
                                """;

                public static String UPDATE_REVIEW_STATUS = """
                                UPDATE REVIEWS SET status = ?
                                WHERE id = ? and status = 'pending'
                                """;

                public static String UPDATE_USER = """
                                UPDATE USERS set
                                name = ?, nick_name = ?, email = ?, password = ?,
                                team_id = (SELECT id from TEAMS WHERE name = ? limit 1),
                                manager_id = (SELECT id from USERS WHERE username = ? limit 1),
                                job_id = (SELECT id from JOBS WHERE name = ? limit 1),
                                is_live = true
                                WHERE id = (SELECT id from USERS WHERE username = ? limit 1)
                                """;

                public static String UPDATE_TEAM = """
                                UPDATE TEAMS set name = ? WHERE name = ?
                                """;

                public static String UPDATE_JOB = """
                                UPDATE JOBS set name = ? WHERE id = ?
                                """;
                // #endregion

                // #region DELETE records
                public static String DELETE_USER = """
                                DELETE FROM REVIEWS WHERE review_for = (SELECT id FROM USERS WHERE username = ?) or
                                submitted_by = (SELECT id FROM USERS WHERE username = ?);
                                DELETE FROM USERS WHERE username = ?;
                                """;

                public static String DELETE_TEAM = """
                                DELETE FROM TEAMS WHERE name = ?
                                """;;

                public static String DELETE_JOB = """
                                DELETE FROM JOBS WHERE id = ?
                                """;
                // #endregion

                // get all users under the manager and check if any of them has pending reviews
                public static String SELECT_PENDING_REVIEWS_FOR_MANAGER = """
                                SELECT u.username, u.name, m.name, j.name, t.name
                                FROM USERS u
                                LEFT OUTER JOIN TEAMS t ON t.id = u.team_id
                                LEFT OUTER JOIN USERS m ON m.id = u.manager_id
                                LEFT OUTER JOIN JOBS j ON j.id = u.job_id
                                WHERE u.manager_id = (SELECT id FROM USERS WHERE username = ? limit 1) and
                                u.is_live = true and
                                u.id in (SELECT review_for FROM REVIEWS WHERE status = 'pending')
                                """;
        }
}
