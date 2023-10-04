/*
 Warnings:
 
 - A unique constraint covering the columns `[stopId]` on the table `Shipment` will be added. If there are existing duplicate values, this will fail.
 
 */
-- AlterTable
ALTER TABLE
  "Shipment"
ADD
  COLUMN "stopId" TEXT;

-- AlterTable
ALTER TABLE
  "Task"
ADD
  COLUMN "planId" TEXT,
ADD
  COLUMN "vehicleId" TEXT;

-- CreateIndex
CREATE UNIQUE INDEX "Shipment_stopId_key" ON "Shipment"("stopId");

-- AddForeignKey
ALTER TABLE
  "Task"
ADD
  CONSTRAINT "Task_planId_fkey" FOREIGN KEY ("planId") REFERENCES "Plan"("id") ON DELETE
SET
  NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE
  "Task"
ADD
  CONSTRAINT "Task_vehicleId_fkey" FOREIGN KEY ("vehicleId") REFERENCES "Vehicle"("id") ON DELETE
SET
  NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE
  "Shipment"
ADD
  CONSTRAINT "Shipment_stopId_fkey" FOREIGN KEY ("stopId") REFERENCES "Stop"("id") ON DELETE
SET
  NULL ON UPDATE CASCADE;