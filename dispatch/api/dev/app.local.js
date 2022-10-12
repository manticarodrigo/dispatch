exports.handler = async (event, context) => {
  const { loadFile } = await import('nbb')

  await loadFile('./deps.cljs')

  const { handler } = await loadFile('./src/app.cljs')

  return handler(event, context)
}
