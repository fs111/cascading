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
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 *
 */
@State(Scope.Thread)
public class CanonicalBench
  {
  public enum Canonical
    {
      String,
      Short,
      Short_TYPE,
      Integer,
      Integer_TYPE,
      Long,
      Long_TYPE,
      Float,
      Float_TYPE,
      Double,
      Double_TYPE
    }

  @Param({
    "String",
    "Short",
    "Short_TYPE",
    "Integer",
    "Integer_TYPE",
    "Long",
    "Long_TYPE",
    "Float",
    "Float_TYPE",
    "Double",
    "Double_TYPE"
  })

  Canonical to = Canonical.String;

  Type[] canonicalTypes = new Type[]{
    String.class,
    Short.class,
    Short.TYPE,
    Integer.class,
    Integer.TYPE,
    Long.class,
    Long.TYPE,
    Float.class,
    Float.TYPE,
    Double.class,
    Double.TYPE
  };

  Object[] fromValues = new Object[]{
    null,
    "1000",
    1000,
    1000L,
    1000.000F,
    1000.000D
  };

  Class[] fromTypes = new Class[]{
    String.class,
    String.class,
    Integer.class,
    Long.class,
    Float.class,
    Double.class
  };

  CoercibleType coercibleType;
  ToCanonical[] canonicals;

  @Setup
  public void setup()
    {
    coercibleType = Coercions.coercibleTypeFor( canonicalTypes[ to.ordinal() ] );
    canonicals = new ToCanonical[ fromTypes.length ];

    for( int i = 0; i < fromTypes.length; i++ )
      canonicals[ i ] = coercibleType.from( fromTypes[ i ] );
    }

  @BenchmarkMode({Mode.Throughput})
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  public void baseline( Blackhole bh )
    {
    for( int i = 0; i < fromValues.length; i++ )
      bh.consume( coercibleType.canonical( fromValues[ i ] ) );
    }

  @BenchmarkMode({Mode.Throughput})
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  public void toCanonical( Blackhole bh )
    {
    for( int i = 0; i < fromValues.length; i++ )
      bh.consume( coercibleType.from( fromTypes[ i ] ).canonical( fromValues[ i ] ) );
    }

  @BenchmarkMode({Mode.Throughput})
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Benchmark
  public void toCanonicalFixed( Blackhole bh )
    {
    for( int i = 0; i < fromValues.length; i++ )
      bh.consume( canonicals[ i ].canonical( fromValues[ i ] ) );
    }
  }
