/*
  Warnings:

  - You are about to drop the column `stopId` on the `Shipment` table. All the data in the column will be lost.
  - You are about to drop the column `arrivedAt` on the `Stop` table. All the data in the column will be lost.

*/
-- CreateEnum
CREATE TYPE "StopStatus" AS ENUM ('COMPLETE', 'INCOMPLETE');

-- DropForeignKey
ALTER TABLE "Shipment" DROP CONSTRAINT "Shipment_stopId_fkey";

-- DropForeignKey
ALTER TABLE "Stop" DROP CONSTRAINT "Stop_placeId_fkey";

-- DropIndex
DROP INDEX "Shipment_stopId_key";

-- AlterTable
ALTER TABLE "Shipment" DROP COLUMN "stopId",
ADD COLUMN     "deliveryId" TEXT,
ADD COLUMN     "pickupId" TEXT;

-- AlterTable
ALTER TABLE "Stop" DROP COLUMN "arrivedAt",
ADD COLUMN     "break" BOOLEAN,
ADD COLUMN     "duration" INTEGER,
ADD COLUMN     "finishedAt" TIMESTAMP(3),
ADD COLUMN     "status" "StopStatus",
ALTER COLUMN "placeId" DROP NOT NULL;

-- AddForeignKey
ALTER TABLE "Stop" ADD CONSTRAINT "Stop_placeId_fkey" FOREIGN KEY ("placeId") REFERENCES "Place"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Shipment" ADD CONSTRAINT "Shipment_pickupId_fkey" FOREIGN KEY ("pickupId") REFERENCES "Stop"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Shipment" ADD CONSTRAINT "Shipment_deliveryId_fkey" FOREIGN KEY ("deliveryId") REFERENCES "Stop"("id") ON DELETE SET NULL ON UPDATE CASCADE;
