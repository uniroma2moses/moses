% SCRIPT build_problem_eq_red
%
% This script build the inequalities and equality constraints 
% matrices A, Aeq, B, respectively.
% After defining the auxiliary variables xind and xcode for mapping
% problem variables to a linear index and vice-versa, it sets
% the constraints, namely the load constraints and the QoS constraits


NN=length(node);

bigM=1000;

% variable xind associates integers with index-tuples

xind=[];
riga=0;

contaJ=0;
contaJ1=0;

for k=1:K
    for i=1:NN
        if(~node(i).tipo)
            for J=1:length(node(i).sets)
                riga=riga+1; if k==1 contaJ=contaJ+1; end
                xind(riga,:)=[i J k 0];  % x_iJ^k
            end
            
            for J1=1:length(node(i).sets1)
                riga=riga+1; if k==1 contaJ1=contaJ1+1; end 
                xind(riga,:)=[i J1 k 4]; % x_iJ1^k              
            end
            
        elseif(node(i).tipo==10) % variabili t_v_jl
            riga=riga+1;
            xind(riga,:)=[i 0 k 1];
            for np=1:length(node(i).pathsr)
                riga=riga+1;
                xind(riga,:)=[i np k 1];
            end
        end
    end
end

% variable xcode associates a code to each index-tuple

xcode=[]; maxx=max(xind);

base=[1 maxx(1)+1 (maxx(1)+1)*(maxx(2)+1) ((maxx(1)+1)*(maxx(2)+1))*(maxx(3)+1)]; 

for i=1:riga
    xcode(i,1)=ind_to_code(xind(i,:),base);    
end

nvar=riga;

% build simplex matrix A

A=[]; B=[]; Aeq=[]; Beq=[];

riga=0;

% fork-join constraints

for k=1:K
    for n=(N+1):NN  
        for np=1:length(node(n).pathsr);   
            % constraints t^k_{v_jl}\leq t^k_v_j
            riga=riga+1;
            ind=find(xcode==ind_to_code([n np k 1],base));
            A(riga,ind)=1;
            ind=find(xcode==ind_to_code([n 0 k 1],base));
            A(riga,ind)=-1;
        end
    end
end    


% server capacity constraints
for n=1:N
    if ~node(n).tipo
        for c=1:length(node(n).cc) % forall j\in I.j
            riga=riga+1;
            for J=1:length(node(n).sets) % forall J \in R_i(j)
                if sum(ismember(node(n).sets{J},node(n).cc(c)))  % if j\in J
                    for k=1:K
                        ind=find(xcode==ind_to_code([n J k 0],base));
                        A(riga,ind)=node(n).lambda(k)*Nu(k);
                    end
                end
            end  
            
            for J1=1:length(node(n).sets1)
                if sum(ismember(node(n).sets1{J1},node(n).cc(c)))   
                    app=node(n).sets1{J1};
                    for k=1:K
                        ind=find(xcode==ind_to_code([n J1 k 4],base));
                        if(node(n).cc(c)==app(1)) 
                            A(riga,ind)=node(n).lambda(k)*Nu(k);        
                        else
                            A(riga,ind)=node(n).lambda(k)*Nu(k)*(1-exp(q(app(1),4)));
                        end
                    end
                end
            end
 
            B(riga,1)=cap(node(n).cc(c));
        end
    end
end

% QoS constraints

nrigheQoS=riga;

la_medio=[];
for k=1:K
    app=0;
    for n=1:N    
        if ~node(n).tipo
            app=app+node(n).lambda(k);    
        end
    end
    la_medio(k)=app;
end

for k=1:K
  if gamma(k)  
      
      
    riga=riga+3;
    for n=node(N+1).p{1} % time
        if ~node(n).tipo
            for J=1:length(node(n).sets) % forall J \in R_i(j)   
                ind=find(xcode==ind_to_code([n J k 0],base));     
                A(riga-2,ind)=node(n).lambda(k)/gamma(k)*node(n).sett(J);        
            end            
            for J1=1:length(node(n).sets1)
                ind=find(xcode==ind_to_code([n J1 k 4],base));  
                A(riga-2,ind)=node(n).lambda(k)/gamma(k)*node(n).sett1(J1);      
            end
        elseif node(n).tipo==10
            ind=find(xcode==ind_to_code([n 0 k 1],base));
            A(riga-2,ind)=node(n).lambda(k)/node(N+1).lambda(k);
        end
    end
    
    for n=1:N  % cost and reliability
        if ~node(n).tipo    
            for J=1:length(node(n).sets) % forall J \in R_i(j)   
                ind=find(xcode==ind_to_code([n J k 0],base));     
                A(riga-1,ind)=node(n).lambda(k)/gamma(k)*node(n).setc(J);
                A(riga,ind)=-node(n).lambda(k)/gamma(k)*node(n).setr(J);        
            end
            for J1=1:length(node(n).sets1)
                ind=find(xcode==ind_to_code([n J1 k 4],base));  
                A(riga-1,ind)=node(n).lambda(k)/gamma(k)*node(n).setc1(J1);
                A(riga,ind)=-node(n).lambda(k)/gamma(k)*node(n).setr1(J1);              
            end
        end
    end
    B(riga-2:riga,1)=QoS(k,[ 1 2 4])';
    B(riga,1)=-B(riga,1);
    
  end  
end

% \sum x_iJ=1

nrigheA=riga;

riga=0;

for n=1:N
    if ~node(n).tipo
        for k=1:K
            riga=riga+1;
            for J=1:length(node(n).sets);
                ind=find(xcode==ind_to_code([n J k 0],base));
                Aeq(riga,ind)=1;
            end
            for J1=1:length(node(n).sets1);
                ind=find(xcode==ind_to_code([n J1 k 4],base));
                Aeq(riga,ind)=1;
            end
            Beq(riga,1)=1;
        end
    end
end

for k=1:K
  if gamma(k)
    for n=(N+1):NN  % fork-join expressions
        for np=1:length(node(n).pathsr);   
            
            riga=riga+1;
            
            ind=find(xcode==ind_to_code([n np k 1],base));
            Aeq(riga,ind)=-1; 
            app=node(n).p{np};
            for i=1:length(app)
                napp=app(i);
                if ~node(napp).tipo
                    for J=1:length(node(napp).sets);
                        ind=find(xcode==ind_to_code([napp J k 0],base));
                        Aeq(riga,ind)=node(napp).lambda(k)/node(n).lambda(k)*...
                                    node(napp).sett(J);  
                    end
                    for J1=1:length(node(napp).sets1);
                        ind=find(xcode==ind_to_code([napp J1 k 4],base));
                        Aeq(riga,ind)=node(napp).lambda(k)/node(n).lambda(k)*...
                                    node(napp).sett1(J1);  
                    end
                elseif node(napp).tipo==10
                    ind=find(xcode==ind_to_code([napp 0 k 1],base));
                    Aeq(riga,ind)=node(napp).lambda(k)/node(n).lambda(k);
                end
            end
        end
        Beq(riga,1)=0;
    end
 end
end    

if(size(Aeq,2)<nvar)
    Aeq(riga,nvar)=0;
end

Neq=riga;