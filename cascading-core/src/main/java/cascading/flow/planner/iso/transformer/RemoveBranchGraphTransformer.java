/*
 * Copyright (c) 2007-2014 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.flow.planner.iso.transformer;

import java.util.Set;

import cascading.flow.FlowElement;
import cascading.flow.planner.ElementGraphs;
import cascading.flow.planner.graph.ElementGraph;
import cascading.flow.planner.iso.expression.ElementExpression;
import cascading.flow.planner.iso.expression.ExpressionGraph;
import cascading.flow.planner.iso.finder.Match;
import cascading.util.Util;

/**
 *
 */
public class RemoveBranchGraphTransformer extends MutateGraphTransformer
  {
  public RemoveBranchGraphTransformer( ExpressionGraph filter )
    {
    super( filter );
    }

  public RemoveBranchGraphTransformer( GraphTransformer graphTransformer, ExpressionGraph filter )
    {
    super( graphTransformer, filter );
    }

  @Override
  protected boolean transformGraphInPlaceUsing( Transform<ElementGraph> transform, ElementGraph graph, Match match )
    {
    Set<FlowElement> remove = match.getCapturedElements( ElementExpression.Capture.Primary );

    if( remove.isEmpty() )
      return false;

    if( remove.size() != 1 )
      throw new IllegalStateException( "too many captured elements" );

    ElementGraphs.removeBranchContaining( graph, Util.getFirst( remove ) );

    return true;
    }
  }