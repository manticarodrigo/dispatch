import { loadFile } from 'nbb'

await loadFile('./deps.cljs')

const { handler } = await loadFile('./src/app.cljs')

export { handler }
