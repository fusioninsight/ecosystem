#!/bin/bash
# 功能：检查本节点在负载均衡器中的运行状态
# 如果telnet的端口存在，则说明正常,否则状态不正常
# 返回值:  0 运行, 2 停止
# 
#
declare -r CURRENT_PATH=$(readlink -f "$0" | xargs dirname)
declare -r CURRENT_NAME=$(basename "${BASH_SOURCE-$0}")
declare operator=$1
declare loadBalance_ip=$2
declare client_localip=$3

declare pack=""
declare port=""

g_logDir="/var/log/Bigdata/loadBalanceAdapt"
g_logFile="${g_logDir}/run.log"
mkdir -p "${g_logDir}"

function LOG()
{
    printf "`date -d today +\"%Y-%m-%d %H:%M:%S\"`,000 $1 $pack ${CURRENT_NAME} ${loadBalance_ip} $2\n" >> "$g_logFile" 2>&1
}

function init()
{
    local file="$CURRENT_PATH/config/balance.ini"
    local ret=$(cat ${file} |grep ${loadBalance_ip} | grep -v ^# | tr -d [:space:]) 
    if [ $? -ne 0 ];then
        LOG "ERROR" " $file loadBalance_ip config is not exist."
        return 1
    fi
    
    ret=$(echo $ret |awk -F"=" '{print $2}')
    pack=$(echo $ret |awk -F"," '{print $1}')
    port=$(echo $ret |awk -F"," '{print $3}')
    
    # 如果日志文件大于30M,则清空日志文件
    local MAX_SIZE=30720
    local logFileSize="$(ls -l --block-size=KB ${g_logFile} | awk '{print $5}' | sed "s/[kK][bB]//g")"
    isGreate=$(echo "${logFileSize} >= ${MAX_SIZE}" | bc)
    # 如果表达式成立,isGreate等于非0
    if [ "${isGreate}" != "0" ]; then
        echo "" > "${g_logFile}"
    fi
    
    return 0
}

function main()
{
    init
    if [ $? -eq 1 ];then
        return 3
    fi
    
    netstat -anp|grep ":${port} " > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        LOG "INFO" "telnet server is running."
        return 0
    else 
        LOG "INFO" "telnet server is stop."
        return 2
    fi
}

main "$@"
