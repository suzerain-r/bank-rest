--changeset rassul:02-insert-users-admin

INSERT INTO users (username, password, role)
VALUES ('admin', '$2a$10$GvBvaTxMSerRWvcVmLg5Ju4t1JhOmhU.pbA7NJ5hwgDgDRxFcVEu6', 'ADMIN');
