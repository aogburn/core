/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.mbui.gui.reification;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.gui.Context;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Entry point for reification of an abstract model to a concrete interface.
 * It does rely on different {@link org.jboss.mbui.gui.reification.strategy.WidgetStrategy}'s
 * for each {@link InteractionUnit} type.
 *
 * @author Harald Pehl
 * @date 11/12/2012
 */
public class ReificationPipeline
{
    private final List<ReificationStep> steps;


    @Inject
    public ReificationPipeline(ReadResourceDescriptionStep readResourceDescriptionStep,
            BuildUserInterfaceStep buildUserInterfaceStep)
    {
        // order is important!
        this.steps = new LinkedList<ReificationStep>();
        this.steps.add(readResourceDescriptionStep);
        this.steps.add(buildUserInterfaceStep);
    }

    public void execute(
            final InteractionUnit interactionUnit,
            final Context context,
            final AsyncCallback<Boolean> outcome)
    {
        for (ReificationStep step : steps)
        {
            step.init(interactionUnit, context);
        }

        Iterator<ReificationStep> iterator = steps.iterator();
        ReificationStep first = iterator.next();
        System.out.println("First would be " + first.getName());

        first.execute(iterator, new ReificationCallback()
        {
            int numResponses;
            boolean overallResult;

            @Override
            public void onSuccess(Boolean successful)
            {
                numResponses++;
                overallResult = successful;
                if (numResponses == steps.size())
                {
                    outcome.onSuccess(overallResult);
                }
            }
        });
    }
}