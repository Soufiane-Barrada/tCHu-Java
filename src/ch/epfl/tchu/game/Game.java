package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * représente le déroulement d'une partie tchu
 *
 * @author Barrada Soufiane(310890)
 * @author Zaaraoui Imane (314994)
 */
public final class Game {

    /**
     * fait jouer une partie de tCHu aux joueurs donnés avec  les billets tickets disponibles
     * et le générateur aléatoire rng est utilisé pour créer l'état initial du jeu et pour mélanger
     * les cartes de la défausse pour en faire une nouvelle pioche
     *
     * @param players
     * @param playerNames
     * @param tickets
     * @param rng
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId,
            String> playerNames, SortedBag<Ticket> tickets, Random rng) {


        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);


        GameState gm = GameState.initial(tickets, rng);
        PlayerId currentPlayer = gm.currentPlayerId();

        Player player1 = players.get(currentPlayer);
        Player player2 = players.get(currentPlayer.next());

        Info info1 = new Info(playerNames.get(currentPlayer));
        Info info2 = new Info(playerNames.get(currentPlayer.next()));

        player1.initPlayers(currentPlayer, playerNames);
        player2.initPlayers(currentPlayer.next(), playerNames);


        informer(player1, player2, info1.willPlayFirst());

        player1.setInitialTicketChoice(gm.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gm = gm.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        player2.setInitialTicketChoice(gm.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gm = gm.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);

        update(player1, player2, gm, gm.playerState(currentPlayer), gm.playerState(currentPlayer.next()));

        gm = gm.withInitiallyChosenTickets(currentPlayer, player1.chooseInitialTickets());
        gm = gm.withInitiallyChosenTickets(currentPlayer.next(), player2.chooseInitialTickets());
        informer(player1, player2, info1.keptTickets(gm.playerState(currentPlayer).ticketCount()));
        informer(player1, player2, info2.keptTickets(gm.playerState(currentPlayer.next()).ticketCount()));

        while (true) {

            currentPlayer = gm.currentPlayerId();
            player1 = players.get(currentPlayer);
            player2 = players.get(currentPlayer.next());
            info1 = new Info(playerNames.get(currentPlayer));
            info2 = new Info(playerNames.get(currentPlayer.next()));


            informer(player1, player2, info1.canPlay());

            update(player1, player2, gm, gm.playerState(currentPlayer), gm.playerState(currentPlayer.next()));
            switch (player1.nextTurn()) {
                case CLAIM_ROUTE:

                    Route route = player1.claimedRoute();
                    SortedBag<Card> claimcards = player1.initialClaimCards();
                    if (route.level() == Route.Level.OVERGROUND) {

                        gm = gm.withClaimedRoute(route, claimcards);
                        informer(player1, player2, info2.claimedRoute(route, claimcards));

                    } else {
                        informer(player1, player2, info2.attemptsTunnelClaim(route, claimcards));
                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        SortedBag<Card> drawnCards;
                        for (int i = 0; i < 3; i++) {
                            gm = gm.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gm.topCard());
                            gm = gm.withMoreDiscardedCards(SortedBag.of(gm.topCard()));
                            gm = gm.withoutTopCard();
                        }

                        drawnCards = drawnCardsBuilder.build();

                        int additionalcardsint = route.additionalClaimCardsCount(claimcards, drawnCards);

                        informer(player1, player2, info2.drewAdditionalCards(drawnCards, additionalcardsint));


                        if (additionalcardsint > 0) {
                            List<SortedBag<Card>> options = gm.playerState(currentPlayer)
                                    .possibleAdditionalCards(additionalcardsint, claimcards, drawnCards);
                            if (options.size() != 0) {
                                SortedBag<Card> reponse = player1.chooseAdditionalCards(options);
                                if (!reponse.isEmpty()) {
                                    claimcards = claimcards.union(reponse);
                                    gm = gm.withClaimedRoute(route, claimcards);
                                    informer(player1, player2, info2.claimedRoute(route, claimcards));
                                } else {
                                    informer(player1, player2, info2.didNotClaimRoute(route));
                                }

                            }


                        } else {
                            gm = gm.withClaimedRoute(route, claimcards);
                            informer(player1, player2, info2.claimedRoute(route, claimcards));
                        }
                    }



                    break;
                case DRAW_CARDS:

                    int iz = player1.drawSlot();
                    gm = gm.withCardsDeckRecreatedIfNeeded(rng);
                    if (iz == -1) {
                        gm = gm.withBlindlyDrawnCard();
                        informer(player1, player2, info1.drewBlindCard());
                    } else {
                        informer(player1, player2, info1.drewVisibleCard(gm.cardState().faceUpCard(iz)));
                        gm = gm.withDrawnFaceUpCard(iz);

                    }

                    update(player1, player2, gm, gm.playerState(currentPlayer), gm.playerState(currentPlayer.next()));


                    iz = player1.drawSlot();
                    gm = gm.withCardsDeckRecreatedIfNeeded(rng);
                    if (iz == -1) {
                        gm = gm.withBlindlyDrawnCard();
                        informer(player1, player2, info1.drewBlindCard());
                    } else {
                        informer(player1, player2, info1.drewVisibleCard(gm.cardState().faceUpCard(iz)));
                        gm = gm.withDrawnFaceUpCard(iz);

                    }


                    break;
                case DRAW_TICKETS:

                    SortedBag<Ticket> ticketsAdditionnels;
                    informer(player1, player2, info1.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                    ticketsAdditionnels = player1.chooseTickets(gm.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    informer(player1, player2, info1.keptTickets(ticketsAdditionnels.size()));

                    gm = gm.withChosenAdditionalTickets(gm.topTickets(Constants.IN_GAME_TICKETS_COUNT), ticketsAdditionnels);


                    break;


                /*default:
                    throw new RuntimeException();*/

            }


            //derniertour = gm.lastTurnBegins() || derniertour == 1 || derniertour == 2 ? derniertour + 1 : derniertour;
            if (gm.currentPlayerId() == gm.lastPlayer()) {
                break;
            }

            if (gm.lastTurnBegins()) {
                informer(player1, player2, info1.lastTurnBegins(gm.playerState(currentPlayer).carCount()));
            }

            gm = gm.forNextTurn();

        }



        Trail player1LongestTrail = Trail.longest(gm.playerState(currentPlayer).routes());
        Trail player2LongestTrail = Trail.longest(gm.playerState(currentPlayer.next()).routes());
        int player1TotalPoints = gm.playerState(currentPlayer).finalPoints();
        int player2TotalPoints = gm.playerState(currentPlayer.next()).finalPoints();

        if (player1LongestTrail.length() > player2LongestTrail.length()) {
            player1TotalPoints += Constants.LONGEST_TRAIL_BONUS_POINTS;
            informer(player1, player2, info1.getsLongestTrailBonus(player1LongestTrail));
        } else if (player1LongestTrail.length() < player2LongestTrail.length()) {
            player2TotalPoints += Constants.LONGEST_TRAIL_BONUS_POINTS;
            informer(player1, player2, info2.getsLongestTrailBonus(player2LongestTrail));

        } else {
            player1TotalPoints += Constants.LONGEST_TRAIL_BONUS_POINTS;
            player2TotalPoints += Constants.LONGEST_TRAIL_BONUS_POINTS;
            informer(player1, player2, info1.getsLongestTrailBonus(player1LongestTrail));
            informer(player1, player2, info2.getsLongestTrailBonus(player2LongestTrail));

        }
        update(player1, player2, gm, gm.playerState(currentPlayer), gm.playerState(currentPlayer.next()));
        if (player1TotalPoints > player2TotalPoints) {
            informer(player1, player2, info1.won(player1TotalPoints, player2TotalPoints));
        } else if (player1TotalPoints < player2TotalPoints) {
            informer(player1, player2, info2.won(player2TotalPoints, player1TotalPoints));

        } else {
            informer(player1, player2, Info.draw(List.of(playerNames.get(currentPlayer),
                    playerNames.get(currentPlayer.next())), player2TotalPoints));

        }


    }

    /**
     * envoie une information générée au cours de la partie à tous les joueurs,
     *
     * @param player1
     * @param player2
     * @param s
     */
    private static void informer(Player player1, Player player2, String s) {
        player1.receiveInfo(s);
        player2.receiveInfo(s);
    }

    /**
     * informe tous les joueurs d'un changement d'état
     *
     * @param player1
     * @param player2
     * @param newState
     * @param ownState1
     * @param ownState2
     */
    private static void update(Player player1, Player player2, PublicGameState newState, PlayerState ownState1, PlayerState ownState2) {
        player1.updateState(newState, ownState1);
        player2.updateState(newState, ownState2);
    }


}
