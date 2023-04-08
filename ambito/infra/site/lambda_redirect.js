exports.handler = (event, context, callback) => {
  const request = event.Records[0].cf.request;
  const headers = request.headers;

  const redirect_url = `https://dispatch.${process.env.DOMAIN}${request.uri}`;

  const response = {
    status: "301",
    statusDescription: "Moved Permanently",
    headers: {
      location: [
        {
          key: "Location",
          value: redirect_url,
        },
      ],
    },
  };

  callback(null, response);
};
