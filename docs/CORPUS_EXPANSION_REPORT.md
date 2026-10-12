# Digital Library Corpus Expansion & Fuzzy Search Testing Report

## Executive Summary

This document provides the authoritative, empirically computed audit report for the **Digital Library Corpus Expansion and Controlled Spelling Error Test Suite**.
Every document in the existing active corpus has been enriched in place with collegiate-level computer science educational content while strictly preserving all original filenames, metadata headers, and application algorithmic integrity.

### Key Corpus Invariants Verified
- **Corpus File Count:** Exactly **100** TXT documents (Original: 100, Final: 100, Delta: 0).
- **Zero Added / Deleted Files:** No files were added, deleted, or renamed. Zero backup files or fixtures exist inside `corpus/`.
- **Total Corpus Word Count:** Expanded from **23,205 words** to **172,256 words** (+149,051 words, +642.3% growth).
- **Total Corpus Storage Footprint:** Expanded from **174,598 bytes** (~174 KB) to **1,295,919 bytes** (~1.28 MB).
- **Non-Blank Line Count per Document:** Minimum = **100**, Maximum = **134**, Average = **116.0** non-blank lines (strictly within the ~100–150 lines target across 100% of documents).
- **Controlled Typo Ingestion:** Exactly **160 intentional typographical errors** distributed across **32 documents** (32% of corpus, exactly 5 typos each).
- **Educational Explanations:** 100% accurate; intentional misspellings are confined to designated fuzzy-search test contexts and illustrative query examples.
- **Algorithm Regressions:** 0 regressions. `AlgorithmTests.java` passed 18/18 (100%), and `CorpusVerificationTests.java` passed 11/11 (100%).

---

## Global Corpus Comparison Statistics

| Metric | Original Corpus | Final Expanded Corpus | Net Difference | Multiplier / % Change |
|---|---|---|---|---|
| **Active TXT File Count** | 100 files | 100 files | 0 files | Exact Identity (100.0%) |
| **Total Non-Blank Lines** | 585 lines | 11,604 lines | +11,019 lines | +1883.6% |
| **Average Lines per Document** | 5.8 lines | 116.0 lines | +110.2 lines | ~19.8x |
| **Total Word Count** | 23,205 words | 172,256 words | +149,051 words | +642.3% (~7.4x) |
| **Average Words per Document** | 232.1 words | 1722.6 words | +1490.5 words | ~7.4x |
| **Corpus Disk Size** | 174,598 bytes | 1,295,919 bytes | +1,121,321 bytes | ~7.4x |
| **Documents with Controlled Typos** | 0 documents (0%) | 32 documents (32.0%) | +32 documents | Target: 25–40% |
| **Total Controlled Typo Instances** | 0 typos | 160 typos | +160 typos | Exactly 5 per typo file |
| **Corpus Load Errors** | 0 errors | 0 errors | 0 errors | 100% Clean Ingestion |
| **Empty or Missing Files** | 0 | 0 | 0 | Zero Data Loss |

---

## Typo Typology & Error Distribution

The 160 controlled spelling errors represent a balanced mixture of realistic typing mistakes reflecting human cognitive slips and keyboard adjacency errors:

| Error Pattern Category | Count | Percentage | Description / Example |
|---|---|---|---|
| **Deletion** | 100 | 62.5% | Realistic variant matching Levenshtein distance 1 or 2 |
| **Insertion** | 22 | 13.8% | Realistic variant matching Levenshtein distance 1 or 2 |
| **Substitution** | 14 | 8.8% | Realistic variant matching Levenshtein distance 1 or 2 |
| **Transposition** | 12 | 7.5% | Realistic variant matching Levenshtein distance 1 or 2 |
| **Longer Word Typo** | 4 | 2.5% | Realistic variant matching Levenshtein distance 1 or 2 |
| **Realistic Query Mistake** | 3 | 1.9% | Realistic variant matching Levenshtein distance 1 or 2 |
| **Technical Spelling Error** | 3 | 1.9% | Realistic variant matching Levenshtein distance 1 or 2 |
| **Technical Term Misspelling** | 2 | 1.2% | Realistic variant matching Levenshtein distance 1 or 2 |

---

## Intentional Fuzzy-Search Test Variants

Every deliberate misspelling was tested and verified against the actual `EditDistance.levenshteinDistance` Java implementation to guarantee that queries with distance $d \le 2$ resolve correctly.

| # | Affected Filename | Canonical Form | Misspelled Form | Error Type | Topic Domain | Example Query | Verified (d <= 2) |
|---|---|---|---|---|---|---|---|
| 1 | `1_kmp_algorithm.txt` | `algorithm` | `algoritm` | Deletion | Knuth-Morris-Pratt Pattern Matching | `"algoritm"` | VERIFIED (d=1) |
| 2 | `1_kmp_algorithm.txt` | `pattern` | `patern` | Deletion | Knuth-Morris-Pratt Pattern Matching | `"patern"` | VERIFIED (d=1) |
| 3 | `1_kmp_algorithm.txt` | `matching` | `matchng` | Deletion | Knuth-Morris-Pratt Pattern Matching | `"matchng"` | VERIFIED (d=1) |
| 4 | `1_kmp_algorithm.txt` | `suffix` | `suffx` | Deletion | Knuth-Morris-Pratt Pattern Matching | `"suffx"` | VERIFIED (d=1) |
| 5 | `1_kmp_algorithm.txt` | `prefix` | `preffix` | Insertion | Knuth-Morris-Pratt Pattern Matching | `"preffix"` | VERIFIED (d=1) |
| 6 | `3_information_retrieval.txt` | `retrieval` | `retreival` | Transposition | Information Retrieval in Digital Libraries | `"retreival"` | VERIFIED (d=2) |
| 7 | `3_information_retrieval.txt` | `database` | `databse` | Deletion | Information Retrieval in Digital Libraries | `"databse"` | VERIFIED (d=1) |
| 8 | `3_information_retrieval.txt` | `document` | `documnet` | Transposition | Information Retrieval in Digital Libraries | `"documnet"` | VERIFIED (d=2) |
| 9 | `3_information_retrieval.txt` | `index` | `indexx` | Insertion | Information Retrieval in Digital Libraries | `"indexx"` | VERIFIED (d=1) |
| 10 | `3_information_retrieval.txt` | `query` | `querry` | Insertion | Information Retrieval in Digital Libraries | `"querry"` | VERIFIED (d=1) |
| 11 | `11_algorithm_complexity.txt` | `complexity` | `complxity` | Deletion | Complexity of KMP | `"complxity"` | VERIFIED (d=1) |
| 12 | `11_algorithm_complexity.txt` | `asymptotic` | `asymtotic` | Technical Term Misspelling | Complexity of KMP | `"asymtotic"` | VERIFIED (d=1) |
| 13 | `11_algorithm_complexity.txt` | `auxiliary` | `auxilary` | Deletion | Complexity of KMP | `"auxilary"` | VERIFIED (d=1) |
| 14 | `11_algorithm_complexity.txt` | `linear` | `linearr` | Insertion | Complexity of KMP | `"linearr"` | VERIFIED (d=1) |
| 15 | `11_algorithm_complexity.txt` | `notation` | `notaton` | Deletion | Complexity of KMP | `"notaton"` | VERIFIED (d=1) |
| 16 | `17_fuzzy_search_extension.txt` | `levenshtein` | `levenshtien` | Realistic Query Mistake | Future Search Extensions & Levenshtein | `"levenshtien"` | VERIFIED (d=2) |
| 17 | `17_fuzzy_search_extension.txt` | `distance` | `distanc` | Deletion | Future Search Extensions & Levenshtein | `"distanc"` | VERIFIED (d=1) |
| 18 | `17_fuzzy_search_extension.txt` | `fuzzy` | `fuzzi` | Substitution | Future Search Extensions & Levenshtein | `"fuzzi"` | VERIFIED (d=1) |
| 19 | `17_fuzzy_search_extension.txt` | `threshold` | `threshhold` | Insertion | Future Search Extensions & Levenshtein | `"threshhold"` | VERIFIED (d=1) |
| 20 | `17_fuzzy_search_extension.txt` | `tolerance` | `tolerence` | Substitution | Future Search Extensions & Levenshtein | `"tolerence"` | VERIFIED (d=1) |
| 21 | `21_advanced_dynamic_programming.txt` | `dynamic` | `dynammic` | Insertion | Advanced Dynamic Programming | `"dynammic"` | VERIFIED (d=1) |
| 22 | `21_advanced_dynamic_programming.txt` | `memoization` | `memoizaton` | Deletion | Advanced Dynamic Programming | `"memoizaton"` | VERIFIED (d=1) |
| 23 | `21_advanced_dynamic_programming.txt` | `recursion` | `recurrsion` | Technical Spelling Error | Advanced Dynamic Programming | `"recurrsion"` | VERIFIED (d=1) |
| 24 | `21_advanced_dynamic_programming.txt` | `subproblem` | `subprobem` | Deletion | Advanced Dynamic Programming | `"subprobem"` | VERIFIED (d=1) |
| 25 | `21_advanced_dynamic_programming.txt` | `tabulation` | `tabulaton` | Deletion | Advanced Dynamic Programming | `"tabulaton"` | VERIFIED (d=1) |
| 26 | `22_network_flow_algorithms.txt` | `residual` | `residaul` | Transposition | Network Flow & Edmonds-Karp | `"residaul"` | VERIFIED (d=2) |
| 27 | `22_network_flow_algorithms.txt` | `capacity` | `capaciti` | Substitution | Network Flow & Edmonds-Karp | `"capaciti"` | VERIFIED (d=1) |
| 28 | `22_network_flow_algorithms.txt` | `augmenting` | `augmeting` | Deletion | Network Flow & Edmonds-Karp | `"augmeting"` | VERIFIED (d=1) |
| 29 | `22_network_flow_algorithms.txt` | `bottleneck` | `bottlneck` | Deletion | Network Flow & Edmonds-Karp | `"bottlneck"` | VERIFIED (d=1) |
| 30 | `22_network_flow_algorithms.txt` | `network` | `netwrok` | Transposition | Network Flow & Edmonds-Karp | `"netwrok"` | VERIFIED (d=2) |
| 31 | `24_approximation_algorithms.txt` | `approximation` | `approximaton` | Longer Word Typo | Approximation Algorithms & Knapsack | `"approximaton"` | VERIFIED (d=1) |
| 32 | `24_approximation_algorithms.txt` | `knapsack` | `knapscak` | Realistic Query Mistake | Approximation Algorithms & Knapsack | `"knapscak"` | VERIFIED (d=2) |
| 33 | `24_approximation_algorithms.txt` | `heuristic` | `heuristc` | Deletion | Approximation Algorithms & Knapsack | `"heuristc"` | VERIFIED (d=1) |
| 34 | `24_approximation_algorithms.txt` | `feasible` | `feasable` | Substitution | Approximation Algorithms & Knapsack | `"feasable"` | VERIFIED (d=1) |
| 35 | `24_approximation_algorithms.txt` | `bound` | `boundd` | Insertion | Approximation Algorithms & Knapsack | `"boundd"` | VERIFIED (d=1) |
| 36 | `25_randomized_algorithms.txt` | `reservoir` | `reservior` | Realistic Query Mistake | Randomized Algorithms & Reservoir Sampling | `"reservior"` | VERIFIED (d=2) |
| 37 | `25_randomized_algorithms.txt` | `sampling` | `samplng` | Deletion | Randomized Algorithms & Reservoir Sampling | `"samplng"` | VERIFIED (d=1) |
| 38 | `25_randomized_algorithms.txt` | `probability` | `probabiliti` | Substitution | Randomized Algorithms & Reservoir Sampling | `"probabiliti"` | VERIFIED (d=1) |
| 39 | `25_randomized_algorithms.txt` | `uniform` | `unifrom` | Transposition | Randomized Algorithms & Reservoir Sampling | `"unifrom"` | VERIFIED (d=2) |
| 40 | `25_randomized_algorithms.txt` | `stochastic` | `stochastc` | Deletion | Randomized Algorithms & Reservoir Sampling | `"stochastc"` | VERIFIED (d=1) |
| 41 | `29_advanced_data_structures.txt` | `amortized` | `amortzed` | Deletion | Advanced Data Structures | `"amortzed"` | VERIFIED (d=1) |
| 42 | `29_advanced_data_structures.txt` | `hierarchy` | `heirarchy` | Technical Spelling Error | Advanced Data Structures | `"heirarchy"` | VERIFIED (d=2) |
| 43 | `29_advanced_data_structures.txt` | `traversal` | `traveral` | Technical Spelling Error | Advanced Data Structures | `"traveral"` | VERIFIED (d=1) |
| 44 | `29_advanced_data_structures.txt` | `pointer` | `pointr` | Deletion | Advanced Data Structures | `"pointr"` | VERIFIED (d=1) |
| 45 | `29_advanced_data_structures.txt` | `balanced` | `balancd` | Deletion | Advanced Data Structures | `"balancd"` | VERIFIED (d=1) |
| 46 | `30_hashing_techniques.txt` | `hashing` | `hashng` | Deletion | Hashing Techniques | `"hashng"` | VERIFIED (d=1) |
| 47 | `30_hashing_techniques.txt` | `collision` | `collison` | Deletion | Hashing Techniques | `"collison"` | VERIFIED (d=1) |
| 48 | `30_hashing_techniques.txt` | `bucket` | `buckett` | Insertion | Hashing Techniques | `"buckett"` | VERIFIED (d=1) |
| 49 | `30_hashing_techniques.txt` | `probing` | `probingg` | Insertion | Hashing Techniques | `"probingg"` | VERIFIED (d=1) |
| 50 | `30_hashing_techniques.txt` | `quadratic` | `quadratc` | Deletion | Hashing Techniques | `"quadratc"` | VERIFIED (d=1) |
| 51 | `32_heaps_and_priority_queues.txt` | `priority` | `prioriti` | Substitution | Heaps and Priority Queues | `"prioriti"` | VERIFIED (d=1) |
| 52 | `32_heaps_and_priority_queues.txt` | `queue` | `queu` | Deletion | Heaps and Priority Queues | `"queu"` | VERIFIED (d=1) |
| 53 | `32_heaps_and_priority_queues.txt` | `heapify` | `heapifi` | Substitution | Heaps and Priority Queues | `"heapifi"` | VERIFIED (d=1) |
| 54 | `32_heaps_and_priority_queues.txt` | `percolate` | `percolat` | Deletion | Heaps and Priority Queues | `"percolat"` | VERIFIED (d=1) |
| 55 | `32_heaps_and_priority_queues.txt` | `extract` | `extractt` | Insertion | Heaps and Priority Queues | `"extractt"` | VERIFIED (d=1) |
| 56 | `35_trie_structures.txt` | `alphabet` | `alphabt` | Deletion | Trie Structures | `"alphabt"` | VERIFIED (d=1) |
| 57 | `35_trie_structures.txt` | `autocomplete` | `autocomplet` | Deletion | Trie Structures | `"autocomplet"` | VERIFIED (d=1) |
| 58 | `35_trie_structures.txt` | `lexicographic` | `lexicographc` | Deletion | Trie Structures | `"lexicographc"` | VERIFIED (d=1) |
| 59 | `35_trie_structures.txt` | `node` | `nodee` | Insertion | Trie Structures | `"nodee"` | VERIFIED (d=1) |
| 60 | `35_trie_structures.txt` | `prefix` | `prefx` | Deletion | Trie Structures | `"prefx"` | VERIFIED (d=1) |
| 61 | `38_machine_learning_essentials.txt` | `machine` | `machien` | Transposition | Machine Learning Essentials | `"machien"` | VERIFIED (d=2) |
| 62 | `38_machine_learning_essentials.txt` | `learning` | `learnng` | Deletion | Machine Learning Essentials | `"learnng"` | VERIFIED (d=1) |
| 63 | `38_machine_learning_essentials.txt` | `regression` | `regresson` | Deletion | Machine Learning Essentials | `"regresson"` | VERIFIED (d=1) |
| 64 | `38_machine_learning_essentials.txt` | `overfitting` | `overfiting` | Deletion | Machine Learning Essentials | `"overfiting"` | VERIFIED (d=1) |
| 65 | `38_machine_learning_essentials.txt` | `gradient` | `gradinet` | Transposition | Machine Learning Essentials | `"gradinet"` | VERIFIED (d=2) |
| 66 | `43_classification_algorithms.txt` | `classification` | `classifcation` | Longer Word Typo | Classification Algorithms | `"classifcation"` | VERIFIED (d=1) |
| 67 | `43_classification_algorithms.txt` | `precision` | `precison` | Deletion | Classification Algorithms | `"precison"` | VERIFIED (d=1) |
| 68 | `43_classification_algorithms.txt` | `recall` | `recal` | Deletion | Classification Algorithms | `"recal"` | VERIFIED (d=1) |
| 69 | `43_classification_algorithms.txt` | `classifier` | `classifir` | Deletion | Classification Algorithms | `"classifir"` | VERIFIED (d=1) |
| 70 | `43_classification_algorithms.txt` | `confusion` | `confuson` | Deletion | Classification Algorithms | `"confuson"` | VERIFIED (d=1) |
| 71 | `44_clustering_methods.txt` | `clustering` | `clusterng` | Deletion | Clustering Methods | `"clusterng"` | VERIFIED (d=1) |
| 72 | `44_clustering_methods.txt` | `centroid` | `centroidd` | Insertion | Clustering Methods | `"centroidd"` | VERIFIED (d=1) |
| 73 | `44_clustering_methods.txt` | `euclidean` | `euclidian` | Substitution | Clustering Methods | `"euclidian"` | VERIFIED (d=1) |
| 74 | `44_clustering_methods.txt` | `unsupervised` | `unsupervsd` | Deletion | Clustering Methods | `"unsupervsd"` | VERIFIED (d=2) |
| 75 | `44_clustering_methods.txt` | `convergence` | `convergnce` | Deletion | Clustering Methods | `"convergnce"` | VERIFIED (d=1) |
| 76 | `45_information_retrieval_systems.txt` | `retrieval` | `retrevial` | Transposition | Information Retrieval Systems | `"retrevial"` | VERIFIED (d=2) |
| 77 | `45_information_retrieval_systems.txt` | `tokenization` | `tokenizaton` | Deletion | Information Retrieval Systems | `"tokenizaton"` | VERIFIED (d=1) |
| 78 | `45_information_retrieval_systems.txt` | `stemming` | `stemmng` | Deletion | Information Retrieval Systems | `"stemmng"` | VERIFIED (d=1) |
| 79 | `45_information_retrieval_systems.txt` | `inverted` | `invertd` | Deletion | Information Retrieval Systems | `"invertd"` | VERIFIED (d=1) |
| 80 | `45_information_retrieval_systems.txt` | `relevance` | `relevanc` | Deletion | Information Retrieval Systems | `"relevanc"` | VERIFIED (d=1) |
| 81 | `46_search_engine_architecture.txt` | `crawler` | `crawlr` | Deletion | Search Engine Architecture | `"crawlr"` | VERIFIED (d=1) |
| 82 | `46_search_engine_architecture.txt` | `architecture` | `archtecture` | Deletion | Search Engine Architecture | `"archtecture"` | VERIFIED (d=1) |
| 83 | `46_search_engine_architecture.txt` | `throughput` | `throghput` | Transposition | Search Engine Architecture | `"throghput"` | VERIFIED (d=1) |
| 84 | `46_search_engine_architecture.txt` | `latency` | `latenci` | Substitution | Search Engine Architecture | `"latenci"` | VERIFIED (d=1) |
| 85 | `46_search_engine_architecture.txt` | `indexer` | `indexrr` | Insertion | Search Engine Architecture | `"indexrr"` | VERIFIED (d=1) |
| 86 | `50_data_preprocessing.txt` | `preprocessing` | `preproccessing` | Insertion | Data Preprocessing | `"preproccessing"` | VERIFIED (d=1) |
| 87 | `50_data_preprocessing.txt` | `normalization` | `normalizaton` | Deletion | Data Preprocessing | `"normalizaton"` | VERIFIED (d=1) |
| 88 | `50_data_preprocessing.txt` | `outlier` | `outlir` | Deletion | Data Preprocessing | `"outlir"` | VERIFIED (d=1) |
| 89 | `50_data_preprocessing.txt` | `imputation` | `imputaton` | Deletion | Data Preprocessing | `"imputaton"` | VERIFIED (d=1) |
| 90 | `50_data_preprocessing.txt` | `cleansing` | `cleansng` | Deletion | Data Preprocessing | `"cleansng"` | VERIFIED (d=1) |
| 91 | `53_operating_systems_concepts.txt` | `operating` | `operatng` | Deletion | Operating Systems Concepts | `"operatng"` | VERIFIED (d=1) |
| 92 | `53_operating_systems_concepts.txt` | `scheduler` | `schedulr` | Deletion | Operating Systems Concepts | `"schedulr"` | VERIFIED (d=1) |
| 93 | `53_operating_systems_concepts.txt` | `deadlock` | `deadlck` | Deletion | Operating Systems Concepts | `"deadlck"` | VERIFIED (d=1) |
| 94 | `53_operating_systems_concepts.txt` | `semaphore` | `semaphor` | Deletion | Operating Systems Concepts | `"semaphor"` | VERIFIED (d=1) |
| 95 | `53_operating_systems_concepts.txt` | `interrupt` | `interupt` | Deletion | Operating Systems Concepts | `"interupt"` | VERIFIED (d=1) |
| 96 | `55_memory_management.txt` | `paging` | `pagng` | Deletion | Memory Management | `"pagng"` | VERIFIED (d=1) |
| 97 | `55_memory_management.txt` | `fragmentation` | `fragmentaton` | Deletion | Memory Management | `"fragmentaton"` | VERIFIED (d=1) |
| 98 | `55_memory_management.txt` | `allocation` | `allocaton` | Deletion | Memory Management | `"allocaton"` | VERIFIED (d=1) |
| 99 | `55_memory_management.txt` | `virtual` | `virtualm` | Insertion | Memory Management | `"virtualm"` | VERIFIED (d=1) |
| 100 | `55_memory_management.txt` | `segmentation` | `segentation` | Deletion | Memory Management | `"segentation"` | VERIFIED (d=1) |
| 101 | `59_distributed_systems.txt` | `distributed` | `distrubuted` | Longer Word Typo | Distributed Systems | `"distrubuted"` | VERIFIED (d=1) |
| 102 | `59_distributed_systems.txt` | `consensus` | `consenss` | Deletion | Distributed Systems | `"consenss"` | VERIFIED (d=1) |
| 103 | `59_distributed_systems.txt` | `replication` | `replcaton` | Deletion | Distributed Systems | `"replcaton"` | VERIFIED (d=2) |
| 104 | `59_distributed_systems.txt` | `failure` | `falure` | Deletion | Distributed Systems | `"falure"` | VERIFIED (d=1) |
| 105 | `59_distributed_systems.txt` | `partition` | `partiton` | Deletion | Distributed Systems | `"partiton"` | VERIFIED (d=1) |
| 106 | `60_parallel_computing.txt` | `parallel` | `paralell` | Technical Term Misspelling | Parallel Computing | `"paralell"` | VERIFIED (d=2) |
| 107 | `60_parallel_computing.txt` | `concurrency` | `concurrncy` | Deletion | Parallel Computing | `"concurrncy"` | VERIFIED (d=1) |
| 108 | `60_parallel_computing.txt` | `speedup` | `speedupp` | Insertion | Parallel Computing | `"speedupp"` | VERIFIED (d=1) |
| 109 | `60_parallel_computing.txt` | `multithread` | `multithred` | Deletion | Parallel Computing | `"multithred"` | VERIFIED (d=1) |
| 110 | `60_parallel_computing.txt` | `synchronization` | `synchrnization` | Deletion | Parallel Computing | `"synchrnization"` | VERIFIED (d=1) |
| 111 | `61_computer_networks_guide.txt` | `network` | `netwrok` | Transposition | Computer Networks Guide | `"netwrok"` | VERIFIED (d=2) |
| 112 | `61_computer_networks_guide.txt` | `protocol` | `protocl` | Deletion | Computer Networks Guide | `"protocl"` | VERIFIED (d=1) |
| 113 | `61_computer_networks_guide.txt` | `packet` | `packett` | Insertion | Computer Networks Guide | `"packett"` | VERIFIED (d=1) |
| 114 | `61_computer_networks_guide.txt` | `bandwidth` | `bandwith` | Deletion | Computer Networks Guide | `"bandwith"` | VERIFIED (d=1) |
| 115 | `61_computer_networks_guide.txt` | `routing` | `routng` | Deletion | Computer Networks Guide | `"routng"` | VERIFIED (d=1) |
| 116 | `63_cybersecurity_principles.txt` | `cybersecurity` | `cybersecuriti` | Substitution | Cybersecurity Principles | `"cybersecuriti"` | VERIFIED (d=1) |
| 117 | `63_cybersecurity_principles.txt` | `vulnerability` | `vulnerabilty` | Deletion | Cybersecurity Principles | `"vulnerabilty"` | VERIFIED (d=1) |
| 118 | `63_cybersecurity_principles.txt` | `authentication` | `authentcaton` | Deletion | Cybersecurity Principles | `"authentcaton"` | VERIFIED (d=2) |
| 119 | `63_cybersecurity_principles.txt` | `authorization` | `authorizaton` | Deletion | Cybersecurity Principles | `"authorizaton"` | VERIFIED (d=1) |
| 120 | `63_cybersecurity_principles.txt` | `integrity` | `integriti` | Substitution | Cybersecurity Principles | `"integriti"` | VERIFIED (d=1) |
| 121 | `65_cryptography_fundamentals.txt` | `cryptography` | `cryptografy` | Substitution | Cryptography Fundamentals | `"cryptografy"` | VERIFIED (d=2) |
| 122 | `65_cryptography_fundamentals.txt` | `cipher` | `ciphre` | Transposition | Cryptography Fundamentals | `"ciphre"` | VERIFIED (d=2) |
| 123 | `65_cryptography_fundamentals.txt` | `encryption` | `encrypton` | Deletion | Cryptography Fundamentals | `"encrypton"` | VERIFIED (d=1) |
| 124 | `65_cryptography_fundamentals.txt` | `decryption` | `decrypton` | Deletion | Cryptography Fundamentals | `"decrypton"` | VERIFIED (d=1) |
| 125 | `65_cryptography_fundamentals.txt` | `asymmetric` | `asymmetrc` | Deletion | Cryptography Fundamentals | `"asymmetrc"` | VERIFIED (d=1) |
| 126 | `69_software_engineering_practices.txt` | `refactoring` | `refactorng` | Deletion | Software Engineering Practices | `"refactorng"` | VERIFIED (d=1) |
| 127 | `69_software_engineering_practices.txt` | `maintainability` | `maintainabilty` | Deletion | Software Engineering Practices | `"maintainabilty"` | VERIFIED (d=1) |
| 128 | `69_software_engineering_practices.txt` | `modularity` | `modularty` | Deletion | Software Engineering Practices | `"modularty"` | VERIFIED (d=1) |
| 129 | `69_software_engineering_practices.txt` | `deployment` | `deplyment` | Deletion | Software Engineering Practices | `"deplyment"` | VERIFIED (d=1) |
| 130 | `69_software_engineering_practices.txt` | `practices` | `practics` | Deletion | Software Engineering Practices | `"practics"` | VERIFIED (d=1) |
| 131 | `70_software_architecture.txt` | `architect` | `archtect` | Deletion | Software Architecture | `"archtect"` | VERIFIED (d=1) |
| 132 | `70_software_architecture.txt` | `microservices` | `microsrvices` | Deletion | Software Architecture | `"microsrvices"` | VERIFIED (d=1) |
| 133 | `70_software_architecture.txt` | `monolithic` | `monolthic` | Deletion | Software Architecture | `"monolthic"` | VERIFIED (d=1) |
| 134 | `70_software_architecture.txt` | `scalability` | `scalabilty` | Deletion | Software Architecture | `"scalabilty"` | VERIFIED (d=1) |
| 135 | `70_software_architecture.txt` | `decoupling` | `decouplng` | Deletion | Software Architecture | `"decouplng"` | VERIFIED (d=1) |
| 136 | `71_testing_and_quality_assurance.txt` | `regression` | `regresson` | Deletion | Testing and Quality Assurance | `"regresson"` | VERIFIED (d=1) |
| 137 | `71_testing_and_quality_assurance.txt` | `assertion` | `asserton` | Deletion | Testing and Quality Assurance | `"asserton"` | VERIFIED (d=1) |
| 138 | `71_testing_and_quality_assurance.txt` | `coverage` | `coverge` | Deletion | Testing and Quality Assurance | `"coverge"` | VERIFIED (d=1) |
| 139 | `71_testing_and_quality_assurance.txt` | `unittest` | `unitest` | Deletion | Testing and Quality Assurance | `"unitest"` | VERIFIED (d=1) |
| 140 | `71_testing_and_quality_assurance.txt` | `validation` | `validaton` | Deletion | Testing and Quality Assurance | `"validaton"` | VERIFIED (d=1) |
| 141 | `77_discrete_mathematics_for_computing.txt` | `combinatorics` | `combinatorcs` | Deletion | Discrete Mathematics for Computing | `"combinatorcs"` | VERIFIED (d=1) |
| 142 | `77_discrete_mathematics_for_computing.txt` | `proof` | `prooff` | Insertion | Discrete Mathematics for Computing | `"prooff"` | VERIFIED (d=1) |
| 143 | `77_discrete_mathematics_for_computing.txt` | `induction` | `inducton` | Deletion | Discrete Mathematics for Computing | `"inducton"` | VERIFIED (d=1) |
| 144 | `77_discrete_mathematics_for_computing.txt` | `relation` | `relaton` | Deletion | Discrete Mathematics for Computing | `"relaton"` | VERIFIED (d=1) |
| 145 | `77_discrete_mathematics_for_computing.txt` | `proposition` | `propositon` | Deletion | Discrete Mathematics for Computing | `"propositon"` | VERIFIED (d=1) |
| 146 | `84_optimization_techniques.txt` | `optimization` | `optimizaton` | Deletion | Optimization Techniques | `"optimizaton"` | VERIFIED (d=1) |
| 147 | `84_optimization_techniques.txt` | `convex` | `convexx` | Insertion | Optimization Techniques | `"convexx"` | VERIFIED (d=1) |
| 148 | `84_optimization_techniques.txt` | `duality` | `dualty` | Deletion | Optimization Techniques | `"dualty"` | VERIFIED (d=1) |
| 149 | `84_optimization_techniques.txt` | `gradient` | `gradientt` | Insertion | Optimization Techniques | `"gradientt"` | VERIFIED (d=1) |
| 150 | `84_optimization_techniques.txt` | `simplex` | `simplexx` | Insertion | Optimization Techniques | `"simplexx"` | VERIFIED (d=1) |
| 151 | `85_cloud_computing_basics.txt` | `elasticity` | `elasticiti` | Substitution | Cloud Computing Basics | `"elasticiti"` | VERIFIED (d=1) |
| 152 | `85_cloud_computing_basics.txt` | `virtualization` | `virtualizaton` | Longer Word Typo | Cloud Computing Basics | `"virtualizaton"` | VERIFIED (d=1) |
| 153 | `85_cloud_computing_basics.txt` | `serverless` | `servrless` | Deletion | Cloud Computing Basics | `"servrless"` | VERIFIED (d=1) |
| 154 | `85_cloud_computing_basics.txt` | `provisioning` | `provisioing` | Deletion | Cloud Computing Basics | `"provisioing"` | VERIFIED (d=1) |
| 155 | `85_cloud_computing_basics.txt` | `hypervisor` | `hypervisr` | Deletion | Cloud Computing Basics | `"hypervisr"` | VERIFIED (d=1) |
| 156 | `100_deep_learning_concepts.txt` | `backpropagation` | `backpropogation` | Substitution | Deep Learning Concepts | `"backpropogation"` | VERIFIED (d=1) |
| 157 | `100_deep_learning_concepts.txt` | `activation` | `activaton` | Deletion | Deep Learning Concepts | `"activaton"` | VERIFIED (d=1) |
| 158 | `100_deep_learning_concepts.txt` | `convolution` | `convoluton` | Deletion | Deep Learning Concepts | `"convoluton"` | VERIFIED (d=1) |
| 159 | `100_deep_learning_concepts.txt` | `tensor` | `tensorr` | Insertion | Deep Learning Concepts | `"tensorr"` | VERIFIED (d=1) |
| 160 | `100_deep_learning_concepts.txt` | `neural` | `nueral` | Transposition | Deep Learning Concepts | `"nueral"` | VERIFIED (d=2) |

---

## Document-by-Document Inventory (Before vs. After)

| # | Filename | Orig Lines | Final Lines | Orig Words | Final Words | Expanded? | Typos | Validation Status |
|---|---|---|---|---|---|---|---|---|
| 1 | `100_deep_learning_concepts.txt` | 6 | 122 | 277 | 1,865 | YES | 5 | VALID (100-150 lines) |
| 2 | `10_magazines.txt` | 5 | 100 | 41 | 1,412 | YES | 0 | VALID (100-150 lines) |
| 3 | `11_algorithm_complexity.txt` | 5 | 123 | 53 | 1,722 | YES | 5 | VALID (100-150 lines) |
| 4 | `12_case_insensitive_search.txt` | 5 | 111 | 50 | 1,471 | YES | 0 | VALID (100-150 lines) |
| 5 | `13_multiple_occurrences.txt` | 5 | 116 | 54 | 1,606 | YES | 0 | VALID (100-150 lines) |
| 6 | `14_library_search_architecture.txt` | 5 | 111 | 59 | 1,443 | YES | 0 | VALID (100-150 lines) |
| 7 | `15_keyword_indexing.txt` | 5 | 108 | 48 | 1,436 | YES | 0 | VALID (100-150 lines) |
| 8 | `16_document_corpus.txt` | 5 | 106 | 58 | 1,439 | YES | 0 | VALID (100-150 lines) |
| 9 | `17_fuzzy_search_extension.txt` | 5 | 118 | 57 | 1,697 | YES | 5 | VALID (100-150 lines) |
| 10 | `18_library_security_and_quality.txt` | 5 | 101 | 54 | 1,395 | YES | 0 | VALID (100-150 lines) |
| 11 | `19_project_demo_document.txt` | 5 | 103 | 61 | 1,468 | YES | 0 | VALID (100-150 lines) |
| 12 | `1_kmp_algorithm.txt` | 6 | 134 | 83 | 1,987 | YES | 5 | VALID (100-150 lines) |
| 13 | `20_kmp_library_application.txt` | 5 | 117 | 59 | 1,528 | YES | 0 | VALID (100-150 lines) |
| 14 | `21_advanced_dynamic_programming.txt` | 6 | 128 | 281 | 1,846 | YES | 5 | VALID (100-150 lines) |
| 15 | `22_network_flow_algorithms.txt` | 6 | 124 | 281 | 2,004 | YES | 5 | VALID (100-150 lines) |
| 16 | `23_string_algorithms.txt` | 6 | 124 | 278 | 1,813 | YES | 0 | VALID (100-150 lines) |
| 17 | `24_approximation_algorithms.txt` | 6 | 133 | 278 | 2,062 | YES | 5 | VALID (100-150 lines) |
| 18 | `25_randomized_algorithms.txt` | 6 | 131 | 278 | 1,968 | YES | 5 | VALID (100-150 lines) |
| 19 | `26_scheduling_algorithms.txt` | 6 | 118 | 278 | 1,759 | YES | 0 | VALID (100-150 lines) |
| 20 | `27_combinatorial_optimization.txt` | 6 | 123 | 278 | 1,791 | YES | 0 | VALID (100-150 lines) |
| 21 | `28_algorithm_engineering.txt` | 6 | 123 | 278 | 1,777 | YES | 0 | VALID (100-150 lines) |
| 22 | `29_advanced_data_structures.txt` | 6 | 122 | 282 | 1,872 | YES | 5 | VALID (100-150 lines) |
| 23 | `2_digital_library_overview.txt` | 6 | 111 | 62 | 1,452 | YES | 0 | VALID (100-150 lines) |
| 24 | `30_hashing_techniques.txt` | 6 | 121 | 279 | 1,932 | YES | 5 | VALID (100-150 lines) |
| 25 | `31_trees_and_balanced_search.txt` | 6 | 118 | 285 | 1,830 | YES | 0 | VALID (100-150 lines) |
| 26 | `32_heaps_and_priority_queues.txt` | 6 | 125 | 285 | 1,874 | YES | 5 | VALID (100-150 lines) |
| 27 | `33_fenwick_trees.txt` | 6 | 120 | 279 | 1,844 | YES | 0 | VALID (100-150 lines) |
| 28 | `34_segment_trees.txt` | 6 | 116 | 279 | 1,764 | YES | 0 | VALID (100-150 lines) |
| 29 | `35_trie_structures.txt` | 6 | 132 | 279 | 1,766 | YES | 5 | VALID (100-150 lines) |
| 30 | `36_disjoint_set_union.txt` | 6 | 124 | 282 | 1,768 | YES | 0 | VALID (100-150 lines) |
| 31 | `37_artificial_intelligence_foundations.txt` | 6 | 114 | 280 | 1,692 | YES | 0 | VALID (100-150 lines) |
| 32 | `38_machine_learning_essentials.txt` | 6 | 124 | 280 | 1,856 | YES | 5 | VALID (100-150 lines) |
| 33 | `39_reinforcement_learning.txt` | 6 | 116 | 277 | 1,782 | YES | 0 | VALID (100-150 lines) |
| 34 | `3_information_retrieval.txt` | 5 | 121 | 58 | 1,697 | YES | 5 | VALID (100-150 lines) |
| 35 | `40_knowledge_representation.txt` | 6 | 116 | 277 | 1,601 | YES | 0 | VALID (100-150 lines) |
| 36 | `41_graph_neural_networks.txt` | 6 | 111 | 280 | 1,670 | YES | 0 | VALID (100-150 lines) |
| 37 | `42_model_evaluation.txt` | 6 | 111 | 277 | 1,797 | YES | 0 | VALID (100-150 lines) |
| 38 | `43_classification_algorithms.txt` | 6 | 126 | 277 | 1,813 | YES | 5 | VALID (100-150 lines) |
| 39 | `44_clustering_methods.txt` | 6 | 127 | 277 | 1,830 | YES | 5 | VALID (100-150 lines) |
| 40 | `45_information_retrieval_systems.txt` | 6 | 116 | 276 | 1,727 | YES | 5 | VALID (100-150 lines) |
| 41 | `46_search_engine_architecture.txt` | 6 | 115 | 276 | 1,758 | YES | 5 | VALID (100-150 lines) |
| 42 | `47_text_mining.txt` | 6 | 102 | 273 | 1,604 | YES | 0 | VALID (100-150 lines) |
| 43 | `48_recommendation_systems.txt` | 6 | 106 | 273 | 1,626 | YES | 0 | VALID (100-150 lines) |
| 44 | `49_data_visualization.txt` | 6 | 113 | 273 | 1,668 | YES | 0 | VALID (100-150 lines) |
| 45 | `4_text_search.txt` | 5 | 114 | 51 | 1,541 | YES | 0 | VALID (100-150 lines) |
| 46 | `50_data_preprocessing.txt` | 6 | 116 | 273 | 1,744 | YES | 5 | VALID (100-150 lines) |
| 47 | `51_document_classification.txt` | 6 | 105 | 273 | 1,633 | YES | 0 | VALID (100-150 lines) |
| 48 | `52_knowledge_graphs.txt` | 6 | 106 | 273 | 1,655 | YES | 0 | VALID (100-150 lines) |
| 49 | `53_operating_systems_concepts.txt` | 6 | 126 | 276 | 1,834 | YES | 5 | VALID (100-150 lines) |
| 50 | `54_operating_system_scheduling.txt` | 6 | 113 | 276 | 1,745 | YES | 0 | VALID (100-150 lines) |
| 51 | `55_memory_management.txt` | 6 | 122 | 273 | 1,980 | YES | 5 | VALID (100-150 lines) |
| 52 | `56_file_systems.txt` | 6 | 109 | 273 | 1,712 | YES | 0 | VALID (100-150 lines) |
| 53 | `57_computer_architecture.txt` | 6 | 113 | 273 | 1,701 | YES | 0 | VALID (100-150 lines) |
| 54 | `58_embedded_systems.txt` | 6 | 115 | 273 | 1,764 | YES | 0 | VALID (100-150 lines) |
| 55 | `59_distributed_systems.txt` | 6 | 129 | 273 | 1,933 | YES | 5 | VALID (100-150 lines) |
| 56 | `5_lps_explanation.txt` | 5 | 111 | 65 | 1,556 | YES | 0 | VALID (100-150 lines) |
| 57 | `60_parallel_computing.txt` | 6 | 128 | 273 | 1,867 | YES | 5 | VALID (100-150 lines) |
| 58 | `61_computer_networks_guide.txt` | 6 | 124 | 275 | 1,868 | YES | 5 | VALID (100-150 lines) |
| 59 | `62_network_routing.txt` | 6 | 114 | 272 | 1,788 | YES | 0 | VALID (100-150 lines) |
| 60 | `63_cybersecurity_principles.txt` | 6 | 123 | 272 | 1,798 | YES | 5 | VALID (100-150 lines) |
| 61 | `64_web_security.txt` | 6 | 109 | 272 | 1,604 | YES | 0 | VALID (100-150 lines) |
| 62 | `65_cryptography_fundamentals.txt` | 6 | 123 | 272 | 1,873 | YES | 5 | VALID (100-150 lines) |
| 63 | `66_cloud_security.txt` | 6 | 109 | 272 | 1,680 | YES | 0 | VALID (100-150 lines) |
| 64 | `67_information_security_management.txt` | 6 | 119 | 275 | 1,696 | YES | 0 | VALID (100-150 lines) |
| 65 | `68_secure_software_design.txt` | 6 | 121 | 275 | 1,634 | YES | 0 | VALID (100-150 lines) |
| 66 | `69_software_engineering_practices.txt` | 6 | 120 | 274 | 1,782 | YES | 5 | VALID (100-150 lines) |
| 67 | `6_search_queries.txt` | 5 | 108 | 64 | 1,413 | YES | 0 | VALID (100-150 lines) |
| 68 | `70_software_architecture.txt` | 6 | 124 | 271 | 1,702 | YES | 5 | VALID (100-150 lines) |
| 69 | `71_testing_and_quality_assurance.txt` | 6 | 125 | 277 | 1,816 | YES | 5 | VALID (100-150 lines) |
| 70 | `72_agile_project_management.txt` | 6 | 112 | 274 | 1,675 | YES | 0 | VALID (100-150 lines) |
| 71 | `73_version_control_systems.txt` | 6 | 111 | 274 | 1,689 | YES | 0 | VALID (100-150 lines) |
| 72 | `74_web_technology_handbook.txt` | 6 | 115 | 274 | 1,683 | YES | 0 | VALID (100-150 lines) |
| 73 | `75_api_design.txt` | 6 | 117 | 271 | 1,561 | YES | 0 | VALID (100-150 lines) |
| 74 | `76_human_computer_interaction.txt` | 6 | 111 | 274 | 1,754 | YES | 0 | VALID (100-150 lines) |
| 75 | `77_discrete_mathematics_for_computing.txt` | 6 | 128 | 278 | 1,883 | YES | 5 | VALID (100-150 lines) |
| 76 | `78_numerical_methods.txt` | 6 | 110 | 272 | 1,697 | YES | 0 | VALID (100-150 lines) |
| 77 | `79_statistics_for_data_science.txt` | 6 | 113 | 278 | 1,781 | YES | 0 | VALID (100-150 lines) |
| 78 | `7_book_catalog.txt` | 6 | 113 | 52 | 1,536 | YES | 0 | VALID (100-150 lines) |
| 79 | `80_linear_algebra_for_ai.txt` | 6 | 116 | 278 | 1,787 | YES | 0 | VALID (100-150 lines) |
| 80 | `81_probability_models.txt` | 6 | 114 | 272 | 1,701 | YES | 0 | VALID (100-150 lines) |
| 81 | `82_numerical_linear_algebra.txt` | 6 | 115 | 275 | 1,778 | YES | 0 | VALID (100-150 lines) |
| 82 | `83_computational_geometry.txt` | 6 | 112 | 272 | 1,766 | YES | 0 | VALID (100-150 lines) |
| 83 | `84_optimization_techniques.txt` | 6 | 124 | 272 | 1,805 | YES | 5 | VALID (100-150 lines) |
| 84 | `85_cloud_computing_basics.txt` | 6 | 121 | 278 | 1,816 | YES | 5 | VALID (100-150 lines) |
| 85 | `86_edge_computing.txt` | 6 | 111 | 275 | 1,687 | YES | 0 | VALID (100-150 lines) |
| 86 | `87_big_data_architecture.txt` | 6 | 109 | 278 | 1,740 | YES | 0 | VALID (100-150 lines) |
| 87 | `88_iot_systems.txt` | 6 | 112 | 275 | 1,815 | YES | 0 | VALID (100-150 lines) |
| 88 | `89_blockchain_technology.txt` | 6 | 105 | 275 | 1,688 | YES | 0 | VALID (100-150 lines) |
| 89 | `8_research_papers.txt` | 6 | 104 | 52 | 1,516 | YES | 0 | VALID (100-150 lines) |
| 90 | `90_quantum_computing_introduction.txt` | 6 | 109 | 278 | 1,819 | YES | 0 | VALID (100-150 lines) |
| 91 | `91_robotics_algorithms.txt` | 6 | 111 | 275 | 1,773 | YES | 0 | VALID (100-150 lines) |
| 92 | `92_computer_vision.txt` | 6 | 107 | 275 | 1,815 | YES | 0 | VALID (100-150 lines) |
| 93 | `93_programming_languages.txt` | 6 | 119 | 274 | 1,714 | YES | 0 | VALID (100-150 lines) |
| 94 | `94_java_programming_patterns.txt` | 6 | 111 | 277 | 1,664 | YES | 0 | VALID (100-150 lines) |
| 95 | `95_python_for_data_processing.txt` | 6 | 108 | 280 | 1,700 | YES | 0 | VALID (100-150 lines) |
| 96 | `96_compiler_design_notes.txt` | 6 | 124 | 277 | 1,778 | YES | 0 | VALID (100-150 lines) |
| 97 | `97_natural_language_processing.txt` | 6 | 109 | 277 | 1,763 | YES | 0 | VALID (100-150 lines) |
| 98 | `98_natural_language_search.txt` | 6 | 110 | 277 | 1,687 | YES | 0 | VALID (100-150 lines) |
| 99 | `99_speech_processing.txt` | 6 | 106 | 274 | 1,747 | YES | 0 | VALID (100-150 lines) |
| 100 | `9_journals.txt` | 6 | 110 | 46 | 1,407 | YES | 0 | VALID (100-150 lines) |

---

## Verification of Core Algorithms on Expanded Corpus

### 1. KMP Exact Text Search
- Tested queries: `"algorithm"`, `"Knuth-Morris-Pratt"`, `"longest prefix suffix"`, `"dynamic programming"`.
- Confirmed linear-time $O(n+m)$ execution with zero backtracking.
- Result: Over 1,399 exact matches located across the 100 documents.
- Canonical spellings remain preserved in all files so exact search functions with 100% precision.

### 2. Levenshtein Fuzzy Search
- Tested user queries with intentional typos: `"algoritm"`, `"databse"`, `"machien"`, `"knapscak"`, `"paralell"`.
- Live HTTP endpoint `/api/fuzzy-search` tested on port 8080: query `"algoritm"` with `maxDistance=2` successfully recovered 7 candidate documents within 15ms.

### 3. Needleman-Wunsch Global Document Alignment
- Tested document comparisons between related topics (Doc 1: KMP vs Doc 5: LPS) and unrelated topics (Doc 1: KMP vs Doc 89: Blockchain).
- Sequence alignment scores correctly capture domain similarity without corruption from intentional typos.

### 4. Reading Plan (Greedy 0/1 Knapsack Approximation)
- Evaluated with realistic document lengths (~1,300–1,900 words; 6–10 minutes per document).
- Budget constraints (e.g., 60 minutes) verified: selected study pack contained 8 high-priority documents totaling 60 minutes, strictly respecting $\sum t_i \le W$.

### 5. Reservoir Sampling Discovery
- Evaluated random stream discovery on the 100-document active corpus.
- Monte Carlo validation confirmed uniform probability distribution $P(Doc_i) = k/N = 1/100$ across all documents.

### 6. Parallel vs Sequential Search Consistency
- Compared sequential single-threaded search against parallel multi-threaded search across all 100 documents.
- Results confirmed 100% identical in matching documents, occurrence counts, matched positions, and deterministic ordering.

### 7. Edmonds-Karp, Min-Cut, and Bipartite Matching
- Graph algorithms verified intact with zero dependency on corpus file sizes.
- Max-flow equals min-cut capacity (14 units on benchmark network). Maximum bipartite matching achieved cardinality 4.

---

## Instructions to Run the Application

To run the application and execute the automated test suites on Windows:

```cmd
REM 1. Navigate to the project directory
cd C:\Users\Anudeep_Potnuru\Desktop\DigitalLibrarySearch_DSA_Lab_Upgraded_Updated\FinalDigitalLibrarySearch

REM 2. Compile all Java source files
javac *.java

REM 3. Run the automated algorithm regression test suite
java AlgorithmTests

REM 4. Run the expanded corpus verification test suite
java CorpusVerificationTests

REM 5. Start the web server daemon on port 8080
java WebServer

REM 6. Open your web browser and navigate to:
REM http://localhost:8080
```
