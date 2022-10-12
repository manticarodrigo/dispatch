const path = require('path')
const express = require('express')
const lambdaLocal = require('lambda-local')

const app = express()

app.use(express.json())

app.use('/*', async (req, res) => {
  const result = await lambdaLocal.execute({
    lambdaPath: path.join(__dirname, './app.local'),
    lambdaHandler: 'handler',
    envfile: path.join(__dirname, '.env'),
    event: {
      httpMethod: req.method,
      path: req.originalUrl,
      headers: req.headers,
      body: req.body,
      queryStringParameters: req.query,
    },
  })

  res.status(result.statusCode).set(result.headers).end(result.body)
})

app.listen(3000, () => console.log('listening on port: 3000'))
