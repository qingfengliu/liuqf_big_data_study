create database test;
create database test_route;

CREATE table test.t1(
                        `id` VARCHAR(255) PRIMARY KEY,
                        `name` VARCHAR(255)
);

CREATE table test.t2(
                        `id` VARCHAR(255) PRIMARY KEY,
                        `name` VARCHAR(255)
);

CREATE table test.t3(
                        `id` VARCHAR(255) PRIMARY KEY,
                        `name` VARCHAR(255)
);

CREATE table test_route.t1(
                              `id` VARCHAR(255) PRIMARY KEY,
                              `name` VARCHAR(255)
);

CREATE table test_route.t2(
                              `id` VARCHAR(255) PRIMARY KEY,
                              `name` VARCHAR(255)
);

CREATE table test_route.t3(
                              `id` VARCHAR(255) PRIMARY KEY,
                              `name` VARCHAR(255)
);

use test;
INSERT INTO t1 VALUES ('1001','zhangsan');
INSERT INTO t1 VALUES ('1002','lisi');
INSERT INTO t1 VALUES ('1003','wangwu');
INSERT INTO t2 VALUES ('1001','zhangsan');
INSERT INTO t2 VALUES ('1002','lisi');
INSERT INTO t2 VALUES ('1003','wangwu');
INSERT INTO t3 VALUES('1001','F');
INSERT INTO t3 VALUES('1002','F');
INSERT INTO t3 VALUES('1003','M');