CREATE TABLE users (
  email varchar(100) NOT NULL,
  password varchar(100) NOT NULL,
  `role` varchar(100) NOT NULL,
  PRIMARY KEY (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE countries (
  country_name varchar (100) NOT NULL,
  PRIMARY KEY (country_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
