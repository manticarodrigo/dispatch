/*
  Warnings:

  - You are about to drop the column `locationId` on the `Agent` table. All the data in the column will be lost.

*/
-- DropForeignKey
ALTER TABLE "Agent" DROP CONSTRAINT "Agent_locationId_fkey";

-- DropIndex
DROP INDEX "Agent_locationId_key";

-- AlterTable
ALTER TABLE "Agent" DROP COLUMN "locationId";
