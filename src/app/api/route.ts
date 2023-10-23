import { NextRequest } from "next/server";
import { startServerAndCreateNextHandler } from "@as-integrations/next";

import { server, prisma } from "~/api.core";

const handler = startServerAndCreateNextHandler<NextRequest>(server, {
  context: async (req) => {
    const session = req.headers.get("authorization");
    return { prisma, session };
  },
});

export { handler as GET, handler as POST };
