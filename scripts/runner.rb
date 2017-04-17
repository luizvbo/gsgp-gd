#!/usr/bin/env ruby
#DATASETS     = %w(airfoil concrete yacht vladislavleva-1 keijzer-5 keijzer-7 keijzer-8)
DATASETS     = %w(wineRed)
#STRATEGIES   = %w(sigmoid minmax percentileminmax-x zscoresigmoid zscoreminmax zscorepercentileminmax-x)
STRATEGIES   = %w(sigmoid minmax percentileminmax-98 percentileminmax-95 percentileminmax-90 percentileminmax-80 zscoresigmoid zscoreminmax zscorepercentileminmax-98 zscorepercentileminmax-95 zscorepercentileminmax-90 zscorepercentileminmax-80)

MASTER_PATH  = "/Users/casadei/experiments/scripts/masterGSGP.param"
PARAMS_PATH  = "/Users/casadei/experiments/scripts/gsgp"
BIN_PATH     = "/Users/casadei/dev/casadei/gsgp-gd/dist/GSGP.jar"

def change_strategy(strategy)
  lines = File.readlines(MASTER_PATH)
  lines[lines.size - 1] = "normalization.strategy = #{strategy}"

  File.open(MASTER_PATH, 'w') { |f| f.write(lines.join) }
end

def print_and_flush(message)
  $stdout.print "#{message}\n"
  $stdout.flush
end

STRATEGIES.each do |strategy|
  print_and_flush "*** Remove existing data."

  %x(rm -rf /tmp/norm-#{strategy})
  %x(rm -rf /tmp/*-sgp)

  print_and_flush "*** Change strategy to: #{strategy}"
  change_strategy(strategy)

  DATASETS.each do |dataset|
    print_and_flush "*** Running experiments for dataset: #{dataset}"

    IO.popen("java -jar -Xmx2048m #{BIN_PATH} -p #{PARAMS_PATH}/#{dataset}.param 2>&1 > /tmp/output.log") do |pipe|
      pipe.each do |line|
        print_and_flush line
      end
    end

    print_and_flush "*** Done!"
  end

  print_and_flush "*** Move data to /tmp/norm-#{strategy} folder"
  %x(mkdir /tmp/norm-#{strategy})
  %x(mv /tmp/*-sgp /tmp/norm-#{strategy})
  %x{mkdir ~/results}
  %x{mv /tmp/norm-* ~/results}
end
