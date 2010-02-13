/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.fpm.pfpgrowth;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.common.Pair;

/**
 * {@link ParallelFPGrowthCombiner} takes each group of dependent transactions and\ compacts it in a
 * TransactionTree structure
 */

public class ParallelFPGrowthCombiner extends
    Reducer<LongWritable,TransactionTree,LongWritable,TransactionTree> {
  
  @Override
  protected void reduce(LongWritable key, Iterable<TransactionTree> values, Context context) throws IOException,
                                                                                            InterruptedException {
    TransactionTree cTree = new TransactionTree();
    int count = 0;
    int node = 0;
    for (TransactionTree tr : values) {
      Iterator<Pair<List<Integer>,Long>> it = tr.getIterator();
      while (it.hasNext()) {
        Pair<List<Integer>,Long> p = it.next();
        node += cTree.addPattern(p.getFirst(), p.getSecond());
        count++;
      }
    }
    
    context.write(key, cTree.getCompressedTree());
    
  }
  
}
