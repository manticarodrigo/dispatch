/*
 Warnings:
 
 - You are about to drop the column `phone` on the `Agent` table. All the data in the column will be lost.
 - You are about to drop the `Device` table. If the table is not empty, all the data it contains will be lost.
 - A unique constraint covering the columns `[userId]` on the table `Agent` will be added. If there are existing duplicate values, this will fail.
 - A unique constraint covering the columns `[phone]` on the table `User` will be added. If there are existing duplicate values, this will fail.
 
 */
-- DropForeignKey
ALTER TABLE
  "Device" DROP CONSTRAINT "Device_agentId_fkey";

-- AlterTable
ALTER TABLE
  "Agent" DROP COLUMN "phone",
ADD
  COLUMN "userId" TEXT;

-- AlterTable
ALTER TABLE
  "User"
ADD
  COLUMN "phone" TEXT,
ALTER COLUMN
  "email" DROP NOT NULL,
ALTER COLUMN
  "password" DROP NOT NULL;

-- DropTable
DROP TABLE "Device";

-- CreateTable
CREATE TABLE "Verification" (
  "id" TEXT NOT NULL,
  "code" INTEGER NOT NULL,
  "userId" TEXT NOT NULL,
  "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT "Verification_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "Verification_id_key" ON "Verification"("id");

-- CreateIndex
CREATE UNIQUE INDEX "Verification_code_key" ON "Verification"("code");

-- CreateIndex
CREATE UNIQUE INDEX "Agent_userId_key" ON "Agent"("userId");

-- CreateIndex
CREATE UNIQUE INDEX "User_phone_key" ON "User"("phone");

-- AddForeignKey
ALTER TABLE
  "Verification"
ADD
  CONSTRAINT "Verification_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE
  "Agent"
ADD
  CONSTRAINT "Agent_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE
SET
  NULL ON UPDATE CASCADE;