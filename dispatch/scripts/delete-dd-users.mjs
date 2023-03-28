import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

const dd_domains = [
  "dtdgco",
  "dtgco",
  "dtdg.co",
  "dtdg.o",
  "ddg.co",
  "dtg.co",
  "dtd.co",
  "dtd.co",
  "tdg.co",
  "tdg.o",
  "synthetics.t.co",
  "synhti.td.co",
  "synthtics.ddg.co",
  "syntheticsdtdg.co",
  "syntheticsdtg.co",
];

dd_domains.forEach(async (domain) => {
  const email_where = {
    email: { contains: domain },
  };

  await prisma.organization.deleteMany({
    where: {
      admin: email_where,
    },
  });

  await prisma.session.deleteMany({
    where: {
      user: email_where,
    },
  });

  await prisma.verification.deleteMany({
    where: {
      user: email_where,
    },
  });

  await prisma.stripe.deleteMany({
    where: {
      organization: { is: null },
    },
  });

  await prisma.user.deleteMany({
    where: email_where,
  });
});
