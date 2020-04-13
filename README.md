# MartinaNichelini-DistributedJobScheduling
<p align="center">
  <img width="100%" src="https://i.imgur.com/tm9mSuM.png" alt="header" />
</p>
<p align="center">
    <img src="https://i.imgur.com/mPb3Qbd.gif" width="180" alt="Politecnico di Milano"/>
</p>

## Contributors
This project has been completely developed by: [Stefano Martina](https://github.com/stefanomartina) and [Alessandro Nichelini](https://github.com/Alenichel).

## Assignment
Implement an infrastructure to manage jobs submitted to a cluster of Executors. Each client may submit a job to any of the executors receiving a job id as a return value. Through such job id, clients may check (contacting the same  executor  they  submitted  the  job  to)  if  the  job  has  been  executed  and  may  retrieve  back  the  results produced by the job.
Executors  communicate  and  coordinate  among  themselves  in  order  to  share  load  such  that  at  each  time  every Executor  is  running  the  same  number  of  jobs  (or  a  number  as  close  as  possible  to  that).  Assume  links  are reliable but processes (i.e., Executors) may fail (and resume back, re-joining the system immediately after). Choose the strategy you find more appropriate to organize communication and coordination. Use stable storage to cope with failures of Executors.
Implement  the  system  in  Java  (or  any  other  language  you  choose)  only  using  basic  communication  facilities (i.e.,  sockets  and  RMI,  in  case  of  Java).  Alternatively,  implement  the  system  in  OMNeT++,  using  an appropriate, abstract model for the system (including the jobs themselves).

## Implementation Overwiew:
Full detail implementation are described in the documentation.

## How to run
Clone or download the zip of the repo. In the delivery folder there are twho jar files:
* ExecutorMain.jar: for the executor
* Client.jar: for the client

Just use:
```
java -jar /path/to/jar/ExecutorMain.jar
```
for running the executor.

```
java -jar /path/to/jar/Client.jar
```
for running the client.

Please mind that:
* Executors use TCP/UDP 9669 and 9670 ports.
* RMI server uses standard port: 1099

If you want to build an executors group over Internet, check if the previous ports are open.
