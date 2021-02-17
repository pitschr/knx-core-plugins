/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.knx.core.plugin.api.v1.controllers;

import io.javalin.http.Context;
import li.pitschmann.knx.core.communication.KnxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller for requesting the statistic from KNX client
 */
public final class StatisticController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(StatisticController.class);

    public StatisticController(final KnxClient knxClient) {
        super(knxClient);
    }

    /**
     * Endpoint for status request to return the current statistic of KNX client
     *
     * @param ctx the Javalin context
     */
    public void getStatistic(final Context ctx) {
        log.trace("Http Statistic Request received");

        final var statistic = getKnxClient().getStatistic();

        ctx.status(HttpServletResponse.SC_OK);
        ctx.json(statistic);
    }
}
