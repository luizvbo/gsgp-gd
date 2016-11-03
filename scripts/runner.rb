#!/usr/bin/env ruby

#DATASETS     = %w(airfoil concrete yacht vladislavleva-1 vladislavleva-4 keijzer-5)
DATASETS     = %w(airfoil concrete yacht)
STRATEGIES   = %w(sigmoid minmax percentileminmax zscoresigmoid zscoreminmax zscorepercentileminmax)

MASTER_PATH  = "/Users/casadei/experiments/scripts/masterGSGP.param"
PARAMS_PATH  = "/Users/casadei/experiments/scripts/gsgp"
BIN_PATH     = "/Users/casadei/dev/casadei/gsgp-gd/dist/GSGP.jar"

def change_strategy(strategy)
  lines = File.readlines(MASTER_PATH)
  lines[lines.size - 1] = "normalization.strategy = #{strategy}"

  File.open(MASTER_PATH, 'w') { |f| f.write(lines.join) }
end

STRATEGIES.each do |strategy|
  puts "*** Remove existing data."

  %x(rm -rf /tmp/norm-#{strategy})
  %x(rm -rf /tmp/*-sgp)

  puts "*** Change strategy to: #{strategy}"
  change_strategy(strategy)

  DATASETS.each do |dataset|
    puts "*** Running experiments for dataset: #{dataset}"

    IO.popen("java -jar -Xmx2048m #{BIN_PATH} -p #{PARAMS_PATH}/#{dataset}.param 2>&1 > output.log") do |pipe|
      pipe.each do |line|
        puts line
      end
    end

    puts "*** Done!"
  end

  puts "*** Move data to /tmp/norm-#{strategy} folder"
  %x(mkdir /tmp/norm-#{strategy})
  %x(mv /tmp/*-sgp /tmp/norm-#{strategy})
end
