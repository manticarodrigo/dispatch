-- Drop the userId foreign key constraints
ALTER TABLE "Agent" DROP CONSTRAINT "Agent_userId_fkey";
ALTER TABLE "Place" DROP CONSTRAINT "Place_userId_fkey";
ALTER TABLE "Task" DROP CONSTRAINT "Task_userId_fkey";

-- Add the organizationId columns with default values
ALTER TABLE "Agent" ADD COLUMN "organizationId" TEXT NOT NULL DEFAULT '';
ALTER TABLE "Place" ADD COLUMN "organizationId" TEXT NOT NULL DEFAULT '';
ALTER TABLE "Task" ADD COLUMN "organizationId" TEXT NOT NULL DEFAULT '';

-- Update the organizationId columns with the appropriate values
UPDATE "Agent"
SET "organizationId" = "Organization"."id"
FROM "Organization"
WHERE "Organization"."adminId" = "Agent"."userId";

UPDATE "Place"
SET "organizationId" = "Organization"."id"
FROM "Organization"
WHERE "Organization"."adminId" = "Place"."userId";

UPDATE "Task"
SET "organizationId" = "Organization"."id"
FROM "Organization"
WHERE "Organization"."adminId" = "Task"."userId";

-- Remove the organizationId default values
ALTER TABLE "Agent" ALTER COLUMN "organizationId" DROP DEFAULT;
ALTER TABLE "Place" ALTER COLUMN "organizationId" DROP DEFAULT;
ALTER TABLE "Task" ALTER COLUMN "organizationId" DROP DEFAULT;

-- Drop the userId columns
ALTER TABLE "Agent" DROP COLUMN "userId";
ALTER TABLE "Place" DROP COLUMN "userId";
ALTER TABLE "Task" DROP COLUMN "userId";

-- Add organizationId foreign key constraints
ALTER TABLE "Agent" ADD CONSTRAINT "Agent_organizationId_fkey" FOREIGN KEY ("organizationId") REFERENCES "Organization"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Place" ADD CONSTRAINT "Place_organizationId_fkey" FOREIGN KEY ("organizationId") REFERENCES "Organization"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "Task" ADD CONSTRAINT "Task_organizationId_fkey" FOREIGN KEY ("organizationId") REFERENCES "Organization"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
