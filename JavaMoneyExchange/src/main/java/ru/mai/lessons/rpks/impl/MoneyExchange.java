package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IMoneyExchange;
import ru.mai.lessons.rpks.exception.ExchangeIsImpossibleException;

import java.util.*;

public class MoneyExchange implements IMoneyExchange {

    public String exchange(Integer sum, String coinDenomination) throws ExchangeIsImpossibleException {

        if (sum <= 0) {
            throw new ExchangeIsImpossibleException("your sum is zero or negative!");
        }

        List<Integer> arrayOfCoins = coinsCastToArrayInt(coinDenomination);

        Map<Integer, Integer> mapCoinsAmount = new HashMap<>();

        Integer res = recursiveCalculation(sum, arrayOfCoins, mapCoinsAmount);

        if (res != 0) {
            throw new ExchangeIsImpossibleException("fail combination!");
        }

        List<Map.Entry<Integer, Integer>> vecPairs = new ArrayList<>(mapCoinsAmount.entrySet());

        vecPairs.sort(Map.Entry.comparingByKey((a, b) -> b - a));

        StringBuilder resultString = new StringBuilder();

        resultString.append(vecPairs.get(0).getKey()).append("[").append(vecPairs.get(0).getValue()).append("]");

        for (int i = 1; i < vecPairs.size(); i++) {
            resultString.append(", ").append(vecPairs.get(i).getKey()).append("[").append(vecPairs.get(i).getValue()).append("]");
        }

        return resultString.toString();
    }

    private List<Integer> coinsCastToArrayInt(String coinsStr) throws ExchangeIsImpossibleException {
        List<Integer> arrayTmp = new ArrayList<>();

        String[] arrayCoins = coinsStr.split(", ");
        for (String coin : arrayCoins) {
            try {
                arrayTmp.add(Integer.parseInt(coin));
            } catch (NumberFormatException ex) {
                throw new ExchangeIsImpossibleException("error from coins_cast_to_array_int, can't cast " + coin + "   " + ex.getMessage());
            }
        }

        Set<Integer> tmp = new HashSet<>(arrayTmp);

        List<Integer> vectorCoins = new ArrayList<>(tmp);

        vectorCoins.sort((a, b) -> b - a);

        return vectorCoins;
    }

    private Integer recursiveCalculation(Integer sum, List<Integer> arrayOfCoins, Map<Integer, Integer> mapCoinsAmount) {
        if (sum == 0) {
            return sum;
        }

        for (Integer coin : arrayOfCoins) {
            if (coin > 0 && coin <= sum) {
                Integer checker = mapCoinsAmount.get(coin);
                if (checker != null) {
                    mapCoinsAmount.put(coin, checker + 1);
                } else {
                    mapCoinsAmount.put(coin, 1);
                }
                Integer result = recursiveCalculation(sum - coin, arrayOfCoins, mapCoinsAmount);

                if (result != 0) {
                    mapCoinsAmount.put(coin, mapCoinsAmount.get(coin) - 1);
                    if (mapCoinsAmount.get(coin) == null) {
                        mapCoinsAmount.remove(coin);
                    }
                } else {
                    return result;
                }
            }
        }

        return -1;

    }

}