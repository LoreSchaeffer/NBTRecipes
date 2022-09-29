package it.multicoredev.nbtr.utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright © 2022 by Lorenzo Magni
 * This file is part of NBTRecipes.
 * NBTRecipes is under "The 3-Clause BSD License", you can find a copy <a href="https://opensource.org/licenses/BSD-3-Clause">here</a>.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
public class Utils {
    public static ItemStack[][] toMatrix(ItemStack[] items) {
        int size = items.length == 9 ? 3 : 2;
        ItemStack[][] matrix = new ItemStack[size][size];

        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = items[i + j + k];
            }

            k += 2;
        }

        return matrix;
    }

    public static boolean compareItems(ItemStack requestedItem, ItemStack givenItem) {
        if (!requestedItem.isSimilar(givenItem)) return false;

        NBTItem requestedNBT = new NBTItem(requestedItem);
        NBTItem givenNBT = new NBTItem(givenItem);

        if (requestedNBT.hasNBTData() && !givenNBT.hasNBTData()) return false;

        return requestedNBT.equals(givenNBT);
    }

    public static ItemStack[][] cutMatrix(ItemStack[][] matrix) {
        ItemStack[][] newMatrix = new ItemStack[2][2];

        boolean isFirstRowEmpty = true;
        for (ItemStack item : matrix[0]) {
            if (!item.getType().equals(Material.AIR)) {
                isFirstRowEmpty = false;
                break;
            }
        }

        boolean isThirdRowEmpty = true;
        for (ItemStack item : matrix[2]) {
            if (!item.getType().equals(Material.AIR)) {
                isThirdRowEmpty = false;
                break;
            }
        }

        boolean isFirstColumnEmpty = true;
        ItemStack[] firstColumn = new ItemStack[]{matrix[0][0], matrix[1][0], matrix[2][0]};
        for (ItemStack item : firstColumn) {
            if (!item.getType().equals(Material.AIR)) {
                isFirstColumnEmpty = false;
                break;
            }
        }

        boolean isThirdColumnEmpty = true;
        ItemStack[] thirdColumn = new ItemStack[]{matrix[0][2], matrix[1][2], matrix[2][2]};
        for (ItemStack item : thirdColumn) {
            if (!item.getType().equals(Material.AIR)) {
                isThirdColumnEmpty = false;
                break;
            }
        }

        if (isFirstRowEmpty && isFirstColumnEmpty) {
            newMatrix[0][0] = matrix[1][1];
            newMatrix[0][1] = matrix[1][2];
            newMatrix[1][0] = matrix[2][1];
            newMatrix[1][1] = matrix[2][2];
        } else if (isFirstRowEmpty && isThirdColumnEmpty) {
            newMatrix[0][0] = matrix[1][0];
            newMatrix[0][1] = matrix[1][1];
            newMatrix[1][0] = matrix[2][0];
            newMatrix[1][1] = matrix[2][1];
        } else if (isThirdRowEmpty && isFirstColumnEmpty) {
            newMatrix[0][0] = matrix[0][1];
            newMatrix[0][1] = matrix[0][2];
            newMatrix[1][0] = matrix[1][1];
            newMatrix[1][1] = matrix[1][2];
        } else if (isThirdRowEmpty && isThirdColumnEmpty) {
            newMatrix[0][0] = matrix[0][0];
            newMatrix[0][1] = matrix[0][1];
            newMatrix[1][0] = matrix[1][0];
            newMatrix[1][1] = matrix[1][1];
        }

        return newMatrix;
    }
}
