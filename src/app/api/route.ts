import { create_next_handler } from "../../../out/api/api.core";

const handler = create_next_handler();

export { handler as GET, handler as POST };
