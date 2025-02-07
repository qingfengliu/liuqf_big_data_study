CREATE DATABASE SCHEDULE_SYSTEM;
USE SCHEDULE_SYSTEM;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS =0;

DROP TABLE IF EXISTS sys_schedule ;
CREATE TABLE sys_schedule(
                             sid int NOT NULL AUTO_INCREMENT,
                             uid int NULL DEFAULT NULL,
                             title varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             completed int(1) NULL DEFAULT NULL,
                             PRIMARY KEY (sid) USING BTREE
)
    ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
                          uid int NOT NULL AUTO_INCREMENT,
                          username varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                          user_pwd varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                          PRIMARY KEY ( uid ) USING BTREE,
                          UNIQUE INDEX username ( username ) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

INSERT INTO sys_user VALUES (1, 'zhangsan','e10adc3949ba59abbe56e057f20f883e');
INSERT INTO sys_user VALUES (2, 'lisi', 'e10adc3949ba59abbe56e057f20f883e');
SET FOREIGN_KEY_CHECKS = 1;