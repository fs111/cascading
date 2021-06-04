/*
 * Copyright (c) 2016-2021 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
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

package cascading.tuple;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import cascading.tuple.coerce.Coercions;
import cascading.tuple.type.CoercibleType;
import cascading.tuple.type.ToCanonical;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 *
 */
@State(Scope.Benchmark)
public class CanonicalBench
  {
  Type[] canonicalTypes = new Type[]{
    String.class,
    Integer.class,
    Integer.TYPE,
    Double.class,
    Double.TYPE
  };

  CoercibleType[] coercibleTypes = Coercions.coercibleArray( canonicalTypes.length, canonicalTypes );

  Object[][] values = new Object[][]{
    {null, null, null, null, null},
    {"1000.000", "1000", "1000", "1000.000", "1000.000"},
    {1000.000f, "1000", "1000", "1000.000", "1000.000"},
    {"1000.000", 1000, 1000, 1000.000D, 1000.000D},
    {10000, 1000D, 1000D, 1000.000F, 1000.000F}
  };

  Type[][] valueTypes = new Type[][]{
    {String.class, Integer.class, Integer.TYPE, Double.class, Double.TYPE},
    {String.class, String.class, String.class, String.class, String.class},
    {Float.TYPE, String.class, String.class, String.class, String.class},
    {String.class, Integer.TYPE, Integer.class, Double.TYPE, Double.class},
    {Integer.class, Double.TYPE, Double.class, Float.TYPE, Float.class}
  };

  @BenchmarkMode({Mode.Throughput})
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  public void baseline( Blackhole bh )
    {
    for( Object[] list : values )
      {
      for( int i = 0; i < coercibleTypes.length; i++ )
        bh.consume( coercibleTypes[ i ].canonical( list[ i ] ) );
      }
    }

  @BenchmarkMode({Mode.Throughput})
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  public void toCanonical( Blackhole bh )
    {
    for( int i = 0; i < values.length; i++ )
      {
      Object[] list = values[ i ];
      Type[] typeList = valueTypes[ i ];
      for( int j = 0; j < coercibleTypes.length; j++ )
        bh.consume( coercibleTypes[ j ].from( typeList[j] ).canonical( list[ j ] ) );
      }
    }

  ToCanonical[][] canonicals;
  {
  canonicals = new ToCanonical[values.length][coercibleTypes.length];
  for( int i = 0; i < values.length; i++ )
    {
    Type[] typeList = valueTypes[ i ];
    for( int j = 0; j < coercibleTypes.length; j++ )
      canonicals[i][j] = coercibleTypes[ j ].from( typeList[j] );
    }
  }

  @BenchmarkMode({Mode.Throughput})
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  public void toCanonicalFixed( Blackhole bh )
    {
    for( int i = 0; i < values.length; i++ )
      {
      Object[] list = values[ i ];
      for( int j = 0; j < coercibleTypes.length; j++ )
        bh.consume( canonicals[i][j].canonical( list[ j ] ) );
      }
    }
  }
