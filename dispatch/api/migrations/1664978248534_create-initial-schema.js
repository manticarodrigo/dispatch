const { PgLiteral } = require("node-pg-migrate");

exports.up = (pgm) => {
  pgm.createTable("users", {
    id: {
      type: "uuid",
      default: new PgLiteral("uuid_generate_v4()"),
      notNull: true,
      primaryKey: true,
    },
    email: { type: "varchar(320)", notNull: true, unique: true },
    password: { type: "varchar(255)", notNull: true },
    firstName: { type: "varchar(80)", notNull: true },
    lastName: { type: "varchar(100)", notNull: true },
    created_at: {
      type: "timestamp",
      notNull: true,
      default: pgm.func("current_timestamp"),
    },
  });
  pgm.createTable("comments", {
    id: "text",
    message: "text",
    time: "text",
    author: "text",
  });
};

exports.down = (pgm) => {
  pgm.dropTable("users");
  pgm.dropTable("comments");
};
