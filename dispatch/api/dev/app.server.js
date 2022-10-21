const path = require('path')
const express = require('express')
const cors = require('cors')
const lambdaLocal = require('lambda-local')
const url = require('url')

// lambdaLocal.setLogger({ ...console, transports: true, log: () => null })

const app = express()

app.use(cors({ origin: '*' }), express.json())

app.use(async (req, res) => {
  const result = await lambdaLocal.execute({
    // verboseLevel: 0,
    lambdaPath: path.join(__dirname, './app.local'),
    lambdaHandler: 'handler',
    envfile: path.join(__dirname, '.env'),
    event: {
      version: '2.0',
      requestContext: {
        http: {
          method: req.method,
        },
      },
      headers: req.headers,
      body: JSON.stringify(req.body),
      rawQueryString: url.parse(req.url).query,
    },
  })

  res.status(result.statusCode).set(result.headers).end(result.body)
})

app.listen(3000, () => console.log('listening on port: 3000'))
