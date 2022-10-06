exports.up = (pgm) => {
  pgm.createExtension('uuid-ossp', {
    ifNotExists: true,
  })
}

exports.down = (pgm) => {
  pgm.dropExtension('uuid-ossp')
}
