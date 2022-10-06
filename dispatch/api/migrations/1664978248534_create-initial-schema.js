const { PgLiteral } = require('node-pg-migrate')

const genId = () => {
  return {
    type: 'uuid',
    default: new PgLiteral('uuid_generate_v4()'),
    notNull: true,
    primaryKey: true,
  }
}

exports.up = (pgm) => {
  pgm.createTable('users', {
    id: genId(),
    email: { type: 'varchar(255)', notNull: true, unique: true },
    password: { type: 'varchar(255)', notNull: true },
    first_name: { type: 'varchar(255)', notNull: true },
    last_name: { type: 'varchar(255)', notNull: true },
    image_url: { type: 'varchar(255)' },
    location: 'point',
    created_at: {
      type: 'timestamp',
      notNull: true,
      default: pgm.func('current_timestamp'),
    },
  })
  pgm.createTable('items', {
    id: genId(),
    name: { type: 'varchar(255)', notNull: true },
    image_url: 'varchar(255)',
  })
  pgm.createTable('receipts', {
    id: genId(),
    signature_url: 'varchar(255)',
    image_url: 'varchar(255)',
  })
  pgm.createTable('orders', {
    id: genId(),
    customer: { type: 'uuid', notNull: true, references: '"users"' },
    location: { type: 'point', notNull: true },
    items: {
      type: 'uuid',
      notNull: true,
      references: '"items"',
      onDelete: 'cascade',
    },
    receipts: {
      type: 'uuid',
      notNull: true,
      references: '"receipts"',
      onDelete: 'cascade',
    },
  })
  pgm.createTable('routes', {
    id: genId(),
    driver: { type: 'uuid', notNull: true, references: '"users"' },
    orders: {
      type: 'uuid',
      notNull: true,
      references: '"orders"',
    },
    origin: 'point',
    destination: 'point',
  })
}

exports.down = (pgm) => {
  pgm.dropTable('users')
  pgm.dropTable('items')
  pgm.dropTable('receipts')
  pgm.dropTable('orders')
  pgm.dropTable('routes')
}
